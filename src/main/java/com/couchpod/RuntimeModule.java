package com.couchpod;

import com.couchpod.api.contributors.ContributorDAO;
import com.couchpod.api.streams.StreamDAO;
import com.couchpod.api.users.UserDAO;
import com.couchpod.authentication.AuthJwtGenerator;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.skife.jdbi.v2.DBI;

import java.io.UnsupportedEncodingException;

public class RuntimeModule implements Module {
    private DbiFactory dbiFactory = new DbiFactory();

    @Provides
    @Inject
    public DBI dbi(ApiConfig configuration) {
        return dbiFactory.getDbi(configuration.getDataSourceFactory());
    }

    @Provides
    @Inject
    public StreamDAO streamDAO(DBI dbi) {
        return dbi.onDemand(StreamDAO.class);
    }

    @Provides
    @Inject
    public UserDAO userDAO(DBI dbi) {
        return dbi.onDemand(UserDAO.class);
    }

    @Provides
    @Inject
    public ContributorDAO contributorDAO(DBI dbi) {
        return dbi.onDemand(ContributorDAO.class);
    }

    @Provides
    @Inject
    public AuthJwtGenerator getTokenGenerator(ApiConfig configuration) throws UnsupportedEncodingException {
        return new AuthJwtGenerator(configuration.getJwtTokenSecret());
    }

    @Override
    public void configure(Binder binder) {
    }
}
