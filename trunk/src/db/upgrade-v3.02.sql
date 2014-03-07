CREATE TABLE access_request (
  access_request_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  registration_id      INTEGER NOT NULL,
  module_type          VARCHAR(1) NOT NULL,
  access_status        VARCHAR(1) NOT NULL,
  request_date         DATETIME NOT NULL,
  user_id              INTEGER,
  use_comment          VARCHAR(1200)
);

CREATE TABLE module (
  module_type    VARCHAR(1) NOT NULL PRIMARY KEY,
  label          VARCHAR(30) NOT NULL
);

INSERT INTO module(module_type, label) VALUES ('A', 'API');
INSERT INTO module(module_type, label) VALUES ('I', 'IHS');
INSERT INTO module(module_type, label) VALUES ('W', 'Web Access');

CREATE TABLE access (
  access_status  VARCHAR(1) NOT NULL PRIMARY KEY,
  label          VARCHAR(30) NOT NULL
);

INSERT INTO access(access_status, label) VALUES ('R', 'Requested');
INSERT INTO access(access_status, label) VALUES ('G', 'Granted');
INSERT INTO access(access_status, label) VALUES ('C', 'Confirmed');
INSERT INTO access(access_status, label) VALUES ('X', 'Revoked');

CREATE TABLE use_log (
  use_log_id       INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  registration_id  INTEGER NOT NULL,
  module_type      VARCHAR(1) NOT NULL,
  use_date         DATETIME NOT NULL,
  install_date     DATETIME,
  software_version VARCHAR(30),
  usage_count      INTEGER NOT NULL
);

CREATE TABLE registration (
  registration_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  registration_key  VARCHAR(30) NOT NULL,
  user_id           INTEGER,
  name              VARCHAR(30),
  title             VARCHAR(120),
  position          VARCHAR(120),
  phone             VARCHAR(30),
  email             VARCHAR(120),
  task_group_id     INTEGER NOT NULL,
  facility          VARCHAR(120),
  accepted_date     DATETIME NOT NULL
);
