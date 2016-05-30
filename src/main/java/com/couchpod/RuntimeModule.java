package com.couchpod;

import com.couchpod.api.streams.StreamDAO;
import com.couchpod.api.streams.StreamResource;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;

public class RuntimeModule implements Module {
    @Provides
    @Named("dbi")
    public DBI dbi(ApiConfiguration configuration) {
        return new DbiFactory().getDbi(configuration.getDataSourceFactory());
    }

    @Provides
    @Inject
    @Named("streamDao")
    public StreamDAO streamDao(@Named("dbi") DBI dbi) {
        return dbi.onDemand(StreamDAO.class);
    }

    @Provides
    @Inject
    public StreamResource streamResource(@Named("streamDao") StreamDAO streamDao) {
        return new StreamResource(streamDao);
    }

    @Override
    public void configure(Binder binder) {

    }
}
