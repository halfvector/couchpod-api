package com.couchpod;


import com.couchpod.healthchecks.LoadBalancerPing;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * API Entry Point
 */
public class ApiBootstrap extends Application<ApiConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(ApiBootstrap.class);

    public static void main(String[] args) throws Exception {
        new ApiBootstrap().run(args);
    }

    @Override
    public void initialize(io.dropwizard.setup.Bootstrap bootstrap) {
        GuiceBundle<ApiConfiguration> guiceBundle = GuiceBundle.<ApiConfiguration>newBuilder()
                .addModule(new RuntimeModule())
                .setConfigClass(ApiConfiguration.class)
                .enableAutoConfig("com.augmate.api")
                .build();
        bootstrap.addBundle(guiceBundle);

        // flyway migration support
        bootstrap.addBundle(new FlywayBundle<ApiConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ApiConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new MultiPartBundle());

        // run db migrations on startup depending on environment settings
        bootstrap.addBundle(new MigrateOnStartupBundle());
    }

    //
    @Override
    public void run(ApiConfiguration configuration, Environment environment) {
        // CORS: allow requests from anywhere
        allowRequestsFromAnywhere(environment);

        // configure jackson json serialization/deserialization
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // makes JsonUnrecognizedPropertyException work

        // register health checks
        environment.healthChecks().register("ping", new LoadBalancerPing());

        String augmateEnvironment = configuration.getEnvironment();
        log.info("Running in {} environment", augmateEnvironment);
    }

    private void allowRequestsFromAnywhere(Environment environment) {
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "Tenant-ID,X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}