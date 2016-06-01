package com.couchpod.api.users;

import com.couchpod.exceptions.DbExceptions;
import com.couchpod.mapping.Mapping;
import com.google.inject.Inject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/users")
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserDAO userDao;

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

}