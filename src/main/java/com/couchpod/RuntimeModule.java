package com.couchpod;

import com.couchpod.api.streams.StreamDAO;
import com.couchpod.api.users.UserDAO;
import com.couchpod.exceptions.DbiExceptionMapper;
import com.google.inject.*;
import org.skife.jdbi.v2.DBI;

public class RuntimeModule implements Module {
    private DbiFactory dbiFactory = new DbiFactory();

    @Provides
    @Inject
    public DBI dbi(ApiConfiguration configuration) {
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

    @Override
    public void configure(Binder binder) {
    }
}
