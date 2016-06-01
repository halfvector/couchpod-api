package com.augmate.test.streams;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface StreamTestingDAO {
    @SqlUpdate("DELETE FROM `streams`")
    public Integer deleteAll();
}