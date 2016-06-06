package com.augmate.test.contributors;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface ContributorTestingDAO {
    @SqlUpdate("DELETE FROM `streamContributors`")
    public Integer deleteAll();
}