package com.couchpod.api.users;

import com.couchpod.ApiConfig;
import com.couchpod.authentication.AuthJwtGenerator;
import com.couchpod.authentication.AuthUser;
import com.couchpod.exceptions.DbExceptions;
import com.couchpod.mapping.Mapping;
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
import java.util.List;

@Path("/users")
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

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
     * Returns 200 (OK), 409 (Conflict)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(UserRegistrationRequestDTO request) {
        UserEntity entity = modelMapper.map(request, UserEntity.class);

        long userId;
        try {
            userId = userDao.insert(entity);

        } catch (Exception error) {
            if (DbExceptions.isConflict(error)) {
                throw new WebApplicationException("User already exists", 409);
            } else {
                throw new WebApplicationException(error, 500);
            }
        }

        TokenDTO token = userTokenService.getToken(userId);
        NewCookie tokenCookie = userTokenService.getNewSessionCookie(token.token, configuration);

        return Response
                .ok()
                .cookie(tokenCookie)
                .entity(userId)
                .build();
    }

    /**
     * Returns 200 (OK)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getListOfAllUsers() {
        List<UserEntity> entities = userDao.getAll();
        return modelMapper.map(entities, Mapping.<UserDTO>list());
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserDetails(@PathParam("id") Long id) {
        UserEntity entity = userDao.getOne(id);

        if (entity == null) {
            throw new WebApplicationException("User not found", 404);
        }

        return modelMapper.map(entity, UserDTO.class);
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserDetails(@Auth AuthUser user) {
        UserEntity entity = userDao.getOne(user.getUserId());

        if (entity == null) {
            throw new WebApplicationException("User not found", 404);
        }

        return modelMapper.map(entity, UserDTO.class);
    }
}