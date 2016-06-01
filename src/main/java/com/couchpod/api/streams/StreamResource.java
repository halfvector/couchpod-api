package com.couchpod.api.streams;

import com.couchpod.exceptions.DbExceptions;
import com.couchpod.mapping.Mapping;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/streams")
public class StreamResource {
    private static final Logger log = LoggerFactory.getLogger(StreamResource.class);

    @Inject
    private StreamDAO streamDao;

    /**
     * Returns 200 (OK), 409 (Conflict)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Long create(CreateStreamRequestDTO createStreamRequestDTO) {
        StreamEntity entity = StreamMapper.fromDTO(createStreamRequestDTO);

        // TODO: use current userId from @Auth object
        entity.userId = 1L;

        try {
            return streamDao.insert(entity);
        } catch (Exception error) {
            if (DbExceptions.isConflict(error)) {
                throw new WebApplicationException("Stream already exists", 409);
            }
            throw new WebApplicationException(error, 500);
        }
    }

    /**
     * Returns 200 (OK)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<StreamDTO> index() {
        List<StreamEntity> entities = streamDao.getAll();
        return Mapping.map(entities, StreamMapper::toDTO);
    }

    /**
     * Returns 200 (OK), 404 (Not Found)
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamDTO one(@PathParam("id") Long id) {
        StreamEntity entity = streamDao.getOne(id);

        if(entity == null) {
            throw new WebApplicationException("Stream not found", 404);
        }

        return StreamMapper.toDTO(entity);
    }

}