package com.couchpod.api.users;

import com.couchpod.ApiConfiguration;
import com.couchpod.AuthJwtGenerator;
import com.couchpod.AuthUser;
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
    private ApiConfiguration configuration;

    @Inject
    private AuthJwtGenerator tokenGenerator;

    private ModelMapper modelMapper = new UserMapper().getModelMapper();

    /**
     * Returns 200 (OK), 409 (Conflict)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Long createUser(UserRegistrationRequestDTO request) {
        UserEntity entity = modelMapper.map(request, UserEntity.class);

        try {
            return userDao.insert(entity);
        } catch (Exception error) {
            if (DbExceptions.isConflict(error)) {
                throw new WebApplicationException("User already exists", 409);
            } else {
                throw new WebApplicationException(error, 500);
            }
        }
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
     * Returns 200 (OK) or 404 (User with given email and password not found)
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDTO dto) {
        if (!userDao.passwordIsCorrect(dto.email, dto.password)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        int maxAge = 60 * 60;
        TokenDTO token = tokenGenerator.generateValidToken(1, maxAge);

        String domain = configuration.cookieDomain;
        NewCookie tokenCookie = new NewCookie(configuration.cookieAccessTokenName,
                token.token, "/", domain, null, maxAge, false, true);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.token = token.token;

        return Response
                .ok()
                .cookie(tokenCookie)
                .entity(loginResponseDTO)
                .build();
    }

    @GET
    @Path("/get_current")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getCurrentUser(@Auth Principal principal) {
        AuthUser user = (AuthUser) principal;
        return getUserDetails(user.getUserId());
    }


    @POST
    @Path("/logout")
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