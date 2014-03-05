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

-- start here

INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (16, "base", "assumeHepASeriesCompleteAtAge", "Assume Hep A Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus should always forecast the next dose due as the standard 10 year booster. ", "", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (17, "tch", "assumeHepASeriesCompleteAtAge", "Assume Hep A Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", 12);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (18, "base", "assumeHepBSeriesCompleteAtAge", "Assume Hep B Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (19, "tch", "assumeHepBSeriesCompleteAtAge", "Assume Hep B Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", 12);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (20, "base", "assumeMMRSeriesCompleteAtAge", "Assume MMR Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (21, "tch", "assumeMMRSeriesCompleteAtAge", "Assume MMR Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", 12);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (22, "base", "assumeVarSeriesCompleteAtAge", "Assume Varicella Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (23, "tch", "assumeVarSeriesCompleteAtAge", "Assume Varicella Series Complete at Age", "Forecaster should assume that a patient that has reached a certain age (for example 18 years) must have already completed the primary series whether or not it is documented in their record and thus  series is complete.", "", 12);

INSERT INTO admin(admin_status, label) VALUES ('A', 'assumed complete or immune' );