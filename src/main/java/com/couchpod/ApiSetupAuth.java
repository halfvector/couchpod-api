package com.couchpod;

import com.couchpod.authentication.AuthJwtAuthenticator;
import com.couchpod.authentication.AuthUser;
import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA256Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.security.Principal;

public class ApiSetupAuth {
    public void configure(JerseyEnvironment jersey, AuthJwtAuthenticator authenticator,
                          byte[] jwtTokenSecret, String cookieAccessTokenName) {
        jersey.register(new AuthDynamicFeature(
                new JWTAuthFilter.Builder<AuthUser>()
                        .setTokenParser(new DefaultJsonWebTokenParser())
                        .setTokenVerifier(new HmacSHA256Verifier(jwtTokenSecret))
                        .setCookieName(cookieAccessTokenName)
                        .setRealm("realm")
                        .setPrefix("Bearer")
                        .setAuthenticator(authenticator)
                        .buildAuthFilter()));

        jersey.register(new AuthValueFactoryProvider.Binder<>(AuthUser.class));
        jersey.register(RolesAllowedDynamicFeature.class);
    }
}