-- mysql -uroot -pgoldenroot < initial.sql

 DROP DATABASE forecast_tester;

 CREATE DATABASE forecast_tester;

 USE forecast_tester;

-- CREATE USER 'ft_web'@'localhost' IDENTIFIED BY 'cArn88rOw';

-- GRANT ALL PRIVILEGES ON forecast_tester.* TO 'ft_web'@'localhost';

CREATE TABLE user 
(
  user_id         INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(30),
  email           VARCHAR(120),
  password        VARCHAR(30),
  organization    VARCHAR(120),
  position        VARCHAR(120),
  phone           VARCHAR(30),
  agreement_id    INTEGER,
  agreement_date  DATETIME,
  selected_task_group_id      INTEGER,
  selected_test_panel_id      INTEGER,
  selected_software_id        INTEGER,
  selected_test_panel_case_id INTEGER,
  selected_test_case_id       INTEGER
);

INSERT INTO user(user_id, name, email, password, organization, position, phone, selected_task_group_id, selected_test_panel_id, selected_software_id, selected_test_panel_case_id, selected_test_case_id) VALUES (1, 'Unknown User', 'unknown@tchforecasttester.org', 'unk', 'TCH', '', '', 2, 2, 2, 2, 2);


CREATE TABLE agreement
(
  agreement_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version_date    DATE NOT NULL,
  agreement_text  VARCHAR(20000)
);

CREATE TABLE expert
(
  expert_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id        INTEGER NOT NULL,
  task_group_id  INTEGER NOT NULL,
  role_status    VARCHAR(1)
);

CREATE TABLE role
(
  role_status    VARCHAR(1) NOT NULL PRIMARY KEY,
  label          VARCHAR(120)
);

INSERT INTO role(role_status, label) VALUES ('A', 'Admin');
INSERT INTO role(role_status, label) VALUES ('E', 'Expert');
INSERT INTO role(role_status, label) VALUES ('V', 'View');

CREATE TABLE task_group
(
  task_group_id       INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label               VARCHAR(120),
  primary_software_id INTEGER
);

CREATE TABLE software
(
  software_id       INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label             VARCHAR(120),
  service_url       VARCHAR(240),
  service_type      VARCHAR(10),
  schedule_name     VARCHAR(120)
);

CREATE TABLE service
(
  service_type     VARCHAR(10) NOT NULL PRIMARY KEY,
  label            VARCHAR(120) NOT NULL
);

INSERT INTO service(service_type, label) VALUES ('web1', 'Web1 Epic Interface');
INSERT INTO service(service_type, label) VALUES ('tch', 'TCH Forecaster Web Service');
INSERT INTO service(service_type, label) VALUES ('swp', 'Software Partners Web Service');

CREATE TABLE forecast_actual
(
  forecast_actual_id     INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_id            INTEGER NOT NULL,
  schedule_name          VARCHAR(120),
  run_date               DATETIME,
  log_text               TEXT,
  test_case_id           INTEGER NOT NULL,
  forecast_item_id       INTEGER NOT NULL,
  dose_number            VARCHAR(20),
  valid_date             DATE,
  due_date               DATE,
  overdue_date           DATE,
  finished_date          DATE,
  vaccine_cvx            VARCHAR(20)
);

CREATE TABLE test_panel
(
  test_panel_id         INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  task_group_id         INTEGER NOT NULL,
  label                 VARCHAR(120)
);

CREATE TABLE test_panel_case
(
  test_panel_case_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_panel_id        INTEGER NOT NULL,
  test_case_id         INTEGER NOT NULL,
  category_name        VARCHAR(120) NOT NULL,
  include_status       VARCHAR(1) NOT NULL,
  result_status        VARCHAR(1),
  test_case_number     VARCHAR(120)
);

CREATE TABLE test_panel_expected
(
  test_panel_expected_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_panel_case_id     INTEGER NOT NULL,
  forecast_expected_id   INTEGER NOT NULL
);

CREATE TABLE test_case
(
  test_case_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label           VARCHAR(120),
  description     VARCHAR(4000),
  eval_date       DATE,
  patient_first   VARCHAR(30),
  patient_last    VARCHAR(30),
  patient_sex     VARCHAR(1),
  patient_dob     DATE
);

CREATE TABLE include
(
  include_status  VARCHAR(1) NOT NULL PRIMARY KEY,
  label           VARCHAR(120)
);

INSERT INTO include(include_status, label) VALUES ('P', 'Proposed');
INSERT INTO include(include_status, label) VALUES ('I', 'Included');
INSERT INTO include(include_status, label) VALUES ('E', 'Excluded');

CREATE TABLE result
(
  result_status  VARCHAR(1) NOT NULL PRIMARY KEY,
  label          VARCHAR(120)
);

INSERT INTO result(result_status, label) VALUES('P', 'Pass');
INSERT INTO result(result_status, label) VALUES('A', 'Accept');
INSERT INTO result(result_status, label) VALUES('F', 'Fail');
INSERT INTO result(result_status, label) VALUES('R', 'Research');
INSERT INTO result(result_status, label) VALUES('I', 'Fixed');

CREATE TABLE forecast_item
(
  forecast_item_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label            VARCHAR(120)  
);

CREATE TABLE forecast_expected
(
  forecast_expected_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_case_id         INTEGER NOT NULL,
  author_user_id       INTEGER NOT NULL,
  forecast_item_id     INTEGER NOT NULL,
  dose_number            VARCHAR(20),
  valid_date             DATE,
  due_date               DATE,
  overdue_date           DATE,
  finished_date          DATE,
  vaccine_cvx            VARCHAR(20)
);

CREATE TABLE test_event
(
  test_event_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_case_id     INTEGER NOT NULL,
  event_id         INTEGER NOT NULL,
  event_date       DATE
);

CREATE TABLE event
(
  event_id         INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label            VARCHAR(120) NOT NULL,
  event_type_code  VARCHAR(1) NOT NULL,
  vaccine_cvx      VARCHAR(20),
  vaccine_mvx      VARCHAR(20)
);

CREATE TABLE event_type
(
  event_type_code  VARCHAR(1) NOT NULL PRIMARY KEY,
  label            VARCHAR(120) NOT NULL
);

INSERT INTO event_type(event_type_code, label) VALUES('V', 'Vaccination');
INSERT INTO event_type(event_type_code, label) VALUES('H', 'History of Disease');
INSERT INTO event_type(event_type_code, label) VALUES('C', 'Contraindication');

CREATE TABLE rating
(
  rating_status   VARCHAR(1) NOT NULL PRIMARY KEY,
  label            VARCHAR(120) NOT NULL
);

INSERT INTO rating(rating_status, label) VALUES('B', 'Best');
INSERT INTO rating(rating_status, label) VALUES('O', 'Okay');
INSERT INTO rating(rating_status, label) VALUES('P', 'Problem');


CREATE TABLE expert_rating
(
  expert_rating_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_expected_id  INTEGER NOT NULL,
  expert_id             INTEGER NOT NULL,
  test_note_id          INTEGER NOT NULL,
  rating_status         VARCHAR(1) NOT NULL
);


CREATE TABLE test_note
(
  test_note_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  test_case_id  INTEGER NOT NULL,
  user_id       INTEGER NOT NULL,
  note_text     VARCHAR(4000),
  note_date     DATETIME NOT NULL
);

CREATE TABLE system_property
(
  system_property_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  property_name  VARCHAR(200) NOT NULL,
  property_value VARCHAR(500)
);