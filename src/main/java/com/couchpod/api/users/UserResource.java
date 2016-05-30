package com.couchpod.api.users;

import com.couchpod.api.streams.CreateStreamRequestDTO;
import com.couchpod.mapping.MapAs;
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

    private UserDAO userDao;

    @Inject
    public UserResource(UserDAO dao) {
        this.userDao = dao;
    }

    /**
     * Returns 200 (OK), 409 (Conflict)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Long create(CreateStreamRequestDTO createStreamRequestDTO) {
        // Use ModelMapper to automatically map request-DTO to db-Entity.
        // When the models begin to drift, add explicit mapping rules.
        UserEntity entity = new ModelMapper().map(createStreamRequestDTO, UserEntity.class);

        return userDao.insert(entity);
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> index() {
        List<UserEntity> entities = userDao.getAll();
        return new ModelMapper().map(entities, MapAs.<UserDTO>list());
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Path("/:id")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO one(Long id) {
        UserEntity entity = userDao.getOne(id);
        return new ModelMapper().map(entity, UserDTO.class);
    }

}