CREATE SCHEMA "societies";

CREATE TABLE "societies"."societies"
(
  "uuid"      BINARY(16) NOT NULL,
  "name"      VARCHAR(45),
  "tag"       VARCHAR(45),
  "clean_tag" VARCHAR(45),
  "created"   TIMESTAMP
);
CREATE TABLE "societies"."members"
(
  "uuid"       BINARY(16) NOT NULL,
  "created"    TIMESTAMP,
  "society"    BINARY(16),
  "lastActive" TIMESTAMP
);
CREATE TABLE "societies"."member_settings"
(
  "subject_uuid" BINARY(16) NOT NULL,
  "target_uuid"  BINARY(16) NOT NULL,
  "setting"      SMALLINT   NOT NULL,
  "value"        BINARY(64)
);
CREATE TABLE "societies"."members_ranks"
(
  "member" BINARY(16) NOT NULL,
  "rank"   BINARY(16) NOT NULL
);
CREATE TABLE "societies"."ranks"
(
  "uuid"     BINARY(16)  NOT NULL,
  "name"     VARCHAR(45) NOT NULL,
  "priority" SMALLINT    NOT NULL
);
CREATE TABLE "societies"."ranks_settings"
(
  "subject_uuid" BINARY(16) NOT NULL,
  "target_uuid"  BINARY(16) NOT NULL,
  "setting"      SMALLINT   NOT NULL,
  "value"        BINARY(64)
);
CREATE TABLE "societies"."relations"
(
  "society"  BINARY(16) NOT NULL,
  "society2" BINARY(16) NOT NULL,
  "type"     SMALLINT
);

CREATE TABLE "societies"."societies_locks"
(
  "id" SMALLINT NOT NULL
);
CREATE TABLE "societies"."societies_ranks"
(
  "society" BINARY(16) NOT NULL,
  "rank"    BINARY(16) NOT NULL
);
CREATE TABLE "societies"."societies_settings"
(
  "subject_uuid" BINARY(16) NOT NULL,
  "target_uuid"  BINARY(16) NOT NULL,
  "setting"      SMALLINT   NOT NULL,
  "value"        BINARY(64)
);
CREATE INDEX FK_MEMBERS_HAS_RANKS_MEMBERS1_IDX ON "societies"."members_ranks" ("member");
CREATE INDEX FK_RANKS_SETTINGS_RANKS1_IDX ON "societies"."ranks_settings" ("subject_uuid");
CREATE UNIQUE INDEX UUID_UNIQUE ON "societies"."societies" ("uuid");
CREATE UNIQUE INDEX TAG_UNIQUE ON "societies"."societies" ("tag");
CREATE INDEX FK_SOCIETIES_HAS_RANKS_SOCIETIES2_IDX ON "societies"."societies_ranks" ("society");
CREATE INDEX FK_SOCIETIES_HAS_RANKS_RANKS2_IDX ON "societies"."societies_ranks" ("rank");
