package com.augmate.test.users;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface UserTestingDAO {
    @SqlUpdate("DELETE FROM `users`")
    public Integer deleteAll();
}