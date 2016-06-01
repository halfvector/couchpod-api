package com.couchpod.api.users;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import com.hubspot.rosetta.jdbi.RosettaMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import java.util.List;

@RegisterMapperFactory(RosettaMapperFactory.class)
public interface UserDAO {
    /**
     * Throw on name unique constraint violation
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO `users` SET fullName=:fullName, email=:email")
    long insert(@BindWithRosetta UserEntity entity);

    @SqlUpdate("UPDATE `users` SET fullName=:fullName WHERE userId = :userId")
    int update(@Bind("userId") long userId, @BindWithRosetta UserEntity entity);

    /**
     * @return 1 when a row is deleted, otherwise 0
     */
    @SqlUpdate("DELETE FROM `users` WHERE userId = :userId")
    int delete(@Bind("userId") long userId);

    @SqlQuery("SELECT * FROM `users`")
    List<UserEntity> getAll();

    @SqlQuery("SELECT * FROM `users` WHERE userId = :userId")
    UserEntity getOne(@Bind("userId") long userId);
}