package com.couchpod.api.contributors;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import com.hubspot.rosetta.jdbi.RosettaMapperFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import java.util.List;

@RegisterMapperFactory(RosettaMapperFactory.class)
public interface ContributorDAO {

    /**
     * Create new relationship or update existing contribution-type
     * PK: (userId, streamId)
     */
    @SqlUpdate("INSERT INTO `streamContributors` SET " +
            "userId=:userId, streamId=:streamId, contributorType=:contributorType " +
            "ON DUPLICATE KEY UPDATE contributorType=:contributorType")
    void setRelationship(@BindWithRosetta ContributorEntity entity);

    /**
     * @return 1 when a row is deleted, otherwise 0
     */
    @SqlUpdate("DELETE FROM `streamContributors` WHERE userId=:userId and streamId=:streamId")
    int deleteRelationship(@BindWithRosetta ContributorEntity entity);

    @SqlQuery("SELECT * FROM `streamContributors`")
    List<ContributorEntity> getAllStreams();

    @SqlQuery("SELECT * FROM `streamContributors` WHERE userId = :userId")
    List<ContributorEntity> getStreamsForUser(@Bind("userId") long userId);
}