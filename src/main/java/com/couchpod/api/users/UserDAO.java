package com.couchpod.api.users;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface UserDAO {
    /**
     * Throw on name unique constraint violation
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO `users` SET fullName=:fullName")
    public Long insert(@BindWithRosetta UserEntity entity);

    @SqlUpdate("UPDATE `users` SET fullName=:fullName WHERE id = :id")
    public Integer update(@Bind("id") Long id, @BindWithRosetta UserEntity entity);

    /**
     * @return 1 when a row is deleted, otherwise 0
     */
    @SqlUpdate("DELETE FROM `users` WHERE id = :id")
    public Integer delete(@Bind("id") Long id);

    @SqlQuery("SELECT * FROM `users`")
    public List<UserEntity> getAll();

    @SqlQuery("SELECT * FROM `users` WHERE id = :id")
    public UserEntity getOne(@Bind("id") Long id);
}