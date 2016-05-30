package com.couchpod.api.streams;

import com.couchpod.mapping.MapAs;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/streams")
public class StreamResource {
    private static final Logger log = LoggerFactory.getLogger(StreamResource.class);

    private StreamDAO streamDao;

    @Inject
    public StreamResource(@Named("streamDao") StreamDAO streamDao) {
        this.streamDao = streamDao;
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
        StreamEntity entity = new ModelMapper().map(createStreamRequestDTO, StreamEntity.class);

        return streamDao.insert(entity);
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<StreamDTO> index() {
        List<StreamEntity> entities = streamDao.getAll();
        return new ModelMapper().map(entities, MapAs.<StreamDTO>list());
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Path("/:id")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamDTO one(Long id) {
        StreamEntity entity = streamDao.getOne(id);
        return new ModelMapper().map(entity, StreamDTO.class);
    }

}