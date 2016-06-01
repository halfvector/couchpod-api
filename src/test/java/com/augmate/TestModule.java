package com.augmate;

import com.augmate.test.streams.StreamTestingDAO;
import com.augmate.test.users.UserTestingDAO;
import com.couchpod.ApiConfiguration;
import com.couchpod.DbiFactory;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.skife.jdbi.v2.DBI;

public class TestModule implements Module {
    private ApiConfiguration configuration;
    private DbiFactory dbiFactory = new DbiFactory();

    public TestModule(ApiConfiguration apiConfiguration) {
        this.configuration = apiConfiguration;
    }

    @Provides
    public DBI dbi() {
        return dbiFactory.getDbi(configuration.getDataSourceFactory());
    }

    @Provides
    @Inject
    public StreamTestingDAO streamDAO(DBI dbi) {
        return dbi.onDemand(StreamTestingDAO.class);
    }

    @Provides
    public UserTestingDAO userDAO(DBI dbi) {
        return dbi.onDemand(UserTestingDAO.class);
    }

    @Override
    public void configure(Binder binder) {

    }
}
