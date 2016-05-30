CREATE TABLE users (
  id            SERIAL PRIMARY KEY,
  fullName      VARCHAR(255),
  createdAt     BIGINT DEFAULT NULL,
  twitterHandle VARCHAR(255)
);

CREATE TABLE streams (
  streamId    SERIAL PRIMARY KEY,
  userId      BIGINT UNSIGNED NOT NULL,
  createdAt   BIGINT DEFAULT NULL,
  name        VARCHAR(255),
  description TEXT,
  visibility  INT UNSIGNED    NOT NULL,

  UNIQUE (userId, name)
);

/*
 * n:m users:streams mapping
 */
CREATE TABLE streamContributors (
  userId          BIGINT UNSIGNED NOT NULL,
  streamId        BIGINT UNSIGNED NOT NULL,
  contributorType INT UNSIGNED    NOT NULL,

  PRIMARY KEY (userId, streamId)
)