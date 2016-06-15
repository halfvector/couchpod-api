package com.couchpod.api.users;

import com.couchpod.ApiConfig;
import com.couchpod.authentication.AuthJwtGenerator;
import com.couchpod.authentication.AuthUser;
import com.google.inject.Inject;
import io.dropwizard.auth.Auth;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.security.Principal;

@Path("/users/tokens")
public class UserTokenResource {
    private static final Logger log = LoggerFactory.getLogger(UserTokenResource.class);

    @Inject
    private UserDAO userDao;

    @Inject
    private ApiConfig configuration;

    @Inject
    private AuthJwtGenerator tokenGenerator;

    @Inject
    private UserTokenService userTokenService;

    private ModelMapper modelMapper = new UserMapper().getModelMapper();

    /**
     * Returns 200 (OK) or 404 (User with given email and password not found)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDTO dto) {
        UserEntity userEntity = userDao.findByAuthentication(dto.email, dto.password);
        if(userEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        TokenDTO token = userTokenService.getToken(userEntity.userId);
        NewCookie tokenCookie = userTokenService.getNewSessionCookie(token.token, configuration);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.token = token.token;

        return Response
                .ok()
                .cookie(tokenCookie)
                .entity(loginResponseDTO)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getCurrentUser(@Auth Principal principal) {
        AuthUser user = (AuthUser) principal;
        UserEntity entity = userDao.getOne(user.getUserId());

        if (entity == null) {
            throw new WebApplicationException("User not found", 404);
        }

        return modelMapper.map(entity, UserDTO.class);
    }

    @DELETE
    public Response logout() {
        String domain = configuration.cookieDomain;
        NewCookie tokenCookie = new NewCookie(configuration.cookieAccessTokenName,
                null, "/", domain, null, 0, false, true);

        return Response
                .ok()
                .cookie(tokenCookie)
                .build();
    }
}