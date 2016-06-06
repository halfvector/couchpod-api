package com.couchpod;

import com.couchpod.authentication.AuthJwtAuthenticator;
import com.couchpod.healthchecks.LoadBalancerPing;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * The entry point. Configure and start API service.
 */
public class ApiServer extends Application<ApiConfig> {
    private static final Logger log = LoggerFactory.getLogger(ApiServer.class);
    private Injector injector;

    public static void main(String... args) throws Exception {
        new ApiServer().run(args);
    }

    @Override
    public void initialize(Bootstrap<ApiConfig> bootstrap) {
        String packageName = getClass().getPackage().getName();

        GuiceBundle<ApiConfig> guiceBundle = GuiceBundle.<ApiConfig>newBuilder()
                .addModule(new RuntimeModule())
                .setConfigClass(ApiConfig.class)
                .enableAutoConfig(packageName)
                .build();

        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new MultiPartBundle());

        injector = guiceBundle.getInjector();
    }

    @Override
    public void run(ApiConfig configuration, Environment environment) throws UnsupportedEncodingException {
        log.info("Running in {} environment", configuration.getEnvironment());

        // CORS: allow requests from anywhere
        new ApiSetupCors().allowRequestsFromAnywhere(environment.servlets());

        // Configure JSON serialization/deserialization
        // SerializationFeature.FAIL_ON_EMPTY_BEANS enables JsonUnrecognizedPropertyException
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Health checks
        environment.healthChecks().register("ping", new LoadBalancerPing());

        // Authentication via JWT
        AuthJwtAuthenticator authenticator = injector.getInstance(AuthJwtAuthenticator.class);
        new ApiSetupAuth().configure(environment.jersey(), authenticator,
                configuration.getJwtTokenSecret(), configuration.cookieAccessTokenName);
    }
}