package com.couchpod;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.ImmutableListContainerFactory;
import io.dropwizard.jdbi.ImmutableSetContainerFactory;
import io.dropwizard.jdbi.OptionalContainerFactory;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DbiFactory {
    private static final Logger log = LoggerFactory.getLogger(DbiFactory.class);

    private DBI dbi = null;

    public DBI getDbi(DataSourceFactory dataSourceFactory) {
        if (dbi == null) {
            dbi = new DBI(createHikariDataSource(dataSourceFactory));
            registerMappersAndContainers(dbi);
        }

        return dbi;
    }

    private void registerMappersAndContainers(DBI dbi) {
        dbi.registerContainerFactory(new ImmutableListContainerFactory());
        dbi.registerContainerFactory(new ImmutableSetContainerFactory());
        dbi.registerContainerFactory(new OptionalContainerFactory());
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
        dbi.registerMapper(new JodaDateTimeMapper());
    }

    /**
     * Hikari is a modern CP, very speed, handles batch statements well and
     * doesn't complain about having to reconnect/repair a broken db connection
     */
    public DataSource createHikariDataSource(DataSourceFactory dataSourceFactory) {
        return new HikariDataSource(getHikariConfig(dataSourceFactory));
    }

    /**
     * All JDBC connections must be configured using this method.
     * This way we can ensure all connections have the same parameters.
     * Including unicode settings, caching, and batch statement rewrites.
     */
    private HikariConfig getHikariConfig(DataSourceFactory dataSourceFactory) {
        HikariConfig config = new HikariConfig();

        config.setUsername(dataSourceFactory.getUser());
        config.setPassword(dataSourceFactory.getPassword());
        config.setJdbcUrl(dataSourceFactory.getUrl() + "?rewriteBatchedStatements=true");
        config.setMinimumIdle(dataSourceFactory.getMinSize());
        config.setIdleTimeout(dataSourceFactory.getMinIdleTime().toMilliseconds());
        config.setInitializationFailFast(false); // continue even if server is down on startup (lazy-init for docker+fig)
        config.setConnectionTimeout(10 * 1000); // timeout in milliseconds
        //config.setLeakDetectionThreshold(5000); // dump stacktraces of db.open() that haven't been closed for threshold
        //config.setMaximumPoolSize(10); // max connections per pool (when reached, new connections will timeout)

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("useUnicode", "yes");
        config.addDataSourceProperty("characterEncoding", "utf8");

        return config;
    }
}
