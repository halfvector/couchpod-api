package com.couchpod.api.streams;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface StreamDAO {
    /**
     * Throw on name unique constraint violation
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO streams SET name=:name, description=:description")
    public Long insert(@BindWithRosetta StreamEntity entity);

    @SqlUpdate("UPDATE streams SET name=:name, description=:description WHERE id = :id")
    public Integer update(@Bind("id") Long id, @BindWithRosetta StreamEntity entity);

    /**
     * @return 1 when a row is deleted, otherwise 0
     */
    @SqlUpdate("DELETE FROM streams WHERE id = :id")
    public Integer delete(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM streams")
    public List<StreamEntity> getAll();

    @SqlQuery("SELECT * FROM streams WHERE id = :id")
    public StreamEntity getOne(@Bind("id") Long id);
}