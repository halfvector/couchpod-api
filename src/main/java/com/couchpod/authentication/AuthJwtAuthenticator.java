package com.couchpod.authentication;

import com.couchpod.api.users.UserDAO;
import com.couchpod.api.users.UserEntity;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthJwtAuthenticator implements Authenticator<JsonWebToken, AuthUser> {
    private static final Logger log = LoggerFactory.getLogger(AuthJwtAuthenticator.class);

    @Inject
    private UserDAO userDAO;

    @Override
    public Optional<AuthUser> authenticate(JsonWebToken token) {

        new ExpiryValidator().validate(token);

        Long userId = Long.parseLong(token.claim().subject());
        UserEntity userEntity = userDAO.getOne(userId);

        if (userEntity == null) {
            log.info("Token contains an invalid user id: " + userId);
            return Optional.absent();
        }

        AuthUser user = new AuthUser(userEntity.userId, userEntity.fullName);

        return Optional.of(user);
    }
}
