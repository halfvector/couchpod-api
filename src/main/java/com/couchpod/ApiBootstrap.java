package com.couchpod;

import com.couchpod.api.users.UserDAO;
import com.couchpod.api.users.UserEntity;
import com.couchpod.healthchecks.LoadBalancerPing;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA256Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

/**
 * The entry point. Configure and start Dropwizard API service.
 */
public class ApiBootstrap extends Application<ApiConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(ApiBootstrap.class);
    private Injector injector;

    public static void main(String... args) throws Exception {
        new ApiBootstrap().run(args);
    }

    @Override
    public void initialize(io.dropwizard.setup.Bootstrap bootstrap) {
        String packageName = getClass().getPackage().getName();

        GuiceBundle<ApiConfiguration> guiceBundle = GuiceBundle.<ApiConfiguration>newBuilder()
                .addModule(new RuntimeModule())
                .setConfigClass(ApiConfiguration.class)
                .enableAutoConfig(packageName)
                .build();
        bootstrap.addBundle(guiceBundle);

        injector = guiceBundle.getInjector();

        bootstrap.addBundle(new MultiPartBundle());

        // run db migrations on startup depending on environment settings
//        bootstrap.addBundle(new MigrateOnStartupBundle());
    }

    //
    @Override
    public void run(ApiConfiguration configuration, Environment environment) throws UnsupportedEncodingException {
        // CORS: allow requests from anywhere
        new CorsConfiguration().allowRequestsFromAnywhere(environment.servlets());

        // configure jackson json serialization/deserialization
        // SerializationFeature.FAIL_ON_EMPTY_BEANS enables JsonUnrecognizedPropertyException
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // register health checks
        environment.healthChecks().register("ping", new LoadBalancerPing());

        String augmateEnvironment = configuration.getEnvironment();
        log.info("Running in {} environment", augmateEnvironment);

        // setup auth
        configureAuth(configuration, environment);
    }

    private void configureAuth(ApiConfiguration configuration, Environment environment) throws UnsupportedEncodingException {
        final byte[] key = configuration.getJwtTokenSecret();

        final JwtConsumer consumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setRequireSubject() // the JWT must have a subject claim
                .setVerificationKey(new HmacKey(key)) // verify the signature with the public key
                .setRelaxVerificationKeyValidation() // relaxes key length requirement
                .build(); // create the JwtConsumer instance

        environment.jersey().register(new AuthDynamicFeature(
                new JWTAuthFilter.Builder<AuthUser>()
                        .setTokenParser(new DefaultJsonWebTokenParser())
                        .setTokenVerifier(new HmacSHA256Verifier(key))
                        .setCookieName(configuration.cookieAccessTokenName)
                        .setRealm("realm")
                        .setPrefix("Bearer")
                        .setAuthenticator(injector.getInstance(AuthJwtAuthenticator.class))
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }
}