package com.couchpod.api.contributors;

import com.couchpod.api.users.UserMapper;
import com.google.inject.Inject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/contributors")
public class ContributorResource {
    private static final Logger log = LoggerFactory.getLogger(ContributorResource.class);

    @Inject
    private ContributorDAO contributorDAO;

    private ModelMapper modelMapper = new UserMapper().getModelMapper();

    /**
     * Returns 200 (OK)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(ContributorDTO dto) {
        ContributorEntity entity = modelMapper.map(dto, ContributorEntity.class);
        contributorDAO.setRelationship(entity);
    }

    /**
     * Returns 200 (OK)
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(ContributorDTO dto) {
        ContributorEntity entity = modelMapper.map(dto, ContributorEntity.class);
        contributorDAO.deleteRelationship(entity);
    }
}