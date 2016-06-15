package com.couchpod.api.users;

import com.couchpod.ApiConfig;
import com.couchpod.authentication.AuthJwtGenerator;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.NewCookie;

public class UserTokenService {
    private static final Logger log = LoggerFactory.getLogger(UserTokenService.class);

    @Inject
    private UserDAO userDao;

    @Inject
    private ApiConfig configuration;

    @Inject
    private AuthJwtGenerator tokenGenerator;

    public TokenDTO getToken(long userId) {
        return tokenGenerator.generateValidToken(userId, configuration.maxSessionAgeSecs);
    }

    public NewCookie getNewSessionCookie(String jwt, ApiConfig configuration) {
        return new NewCookie(configuration.cookieAccessTokenName, jwt, "/",
                configuration.cookieDomain, null, configuration.maxSessionAgeSecs, false, true);
    }

    public NewCookie getDestroySessionCookie() {
        return new NewCookie(configuration.cookieAccessTokenName,
                null, "/", configuration.cookieDomain, null, 0, false, true);

    }
}