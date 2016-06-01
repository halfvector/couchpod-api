package com.couchpod.api.streams;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import com.hubspot.rosetta.jdbi.RosettaMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import java.util.List;

@RegisterMapperFactory(RosettaMapperFactory.class)
public interface StreamDAO {
    /**
     * Throw on name unique constraint violation
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO streams SET name=:name, description=:description, " +
            "userId=:userId, visibility = :visibility")
    public long insert(@BindWithRosetta StreamEntity entity);

    @SqlUpdate("UPDATE streams SET name=:name, description=:description WHERE streamId = :streamId")
    public int update(@Bind("id") long id, @BindWithRosetta StreamEntity entity);

    /**
     * @return 1 when a row is deleted, otherwise 0
     */
    @SqlUpdate("DELETE FROM streams WHERE streamId = :streamId")
    public int delete(@Bind("streamId") long streamId);

    @SqlQuery("SELECT * FROM streams")
    public List<StreamEntity> getAll();

    @SqlQuery("SELECT * FROM streams WHERE streamId = :streamId")
    public StreamEntity getOne(@Bind("streamId") long streamId);
}