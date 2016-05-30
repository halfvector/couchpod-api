package com.couchpod;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Ensures migrations are run on startup when API is in "localhost" mode
 */
public class MigrateOnStartupBundle implements ConfiguredBundle<ApiConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(MigrateOnStartupBundle.class);

    @Override
    public void run(ApiConfiguration configuration, Environment environment) throws Exception {
        if (!configuration.getMigrateOnStartup()) {
            log.info("Configuration says not to migrate on startup. Skipping migrations.");
            return;
        }

        waitForDatabase(configuration);

        DataSource dataSource = new DbiFactory().createHikariDataSource(configuration.getDataSourceFactory());

        log.info("Running migrations..");
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);

        // while it may seem like a good idea to auto "repair" corrupt migration checksums and erase failed migrations using flyway.repair()
        // in reality this misses important common cases, eg:
        //   a migration was applied successfully but then was modified. "repair" updates checksum but misses the change in sql commands.
        //   so during development and local testing everything seems fine. when pushed to dev, the modified migration runs in full.
        //   the resulting remote schema and therefore functionality now differs from what was visible in local dev/test.
        // so we manually repair the db because it ensures nothing is missed in testing

        flyway.migrate();
        log.info("Running migrations.. Done");
    }

    private void waitForDatabase(ApiConfiguration configuration) throws InterruptedException {
        DBI jdbi = new DbiFactory().getDbi(configuration.getDataSourceFactory());

        log.debug("Waiting for Database..");

        for (int i = 0; i < 180; i++) {
            try {
                Handle handle = jdbi.open();
                jdbi.close(handle);
            } catch (Exception err) {
                Throwable cause = err;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }

                log.debug("Waiting for database.. (attempt #{}/180): {}", i + 1, cause.getMessage());
                Thread.sleep(5000);
            }
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
