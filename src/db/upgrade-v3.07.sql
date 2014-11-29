
-- Page 2

CREATE TABLE relative_rule
(
  rule_id        INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  time_period    VARCHAR(500) NOT NULL,
  test_event_id  INTEGER NOT NULL, 
  and_rule_id    INTEGER
);

ALTER TABLE test_case ADD COLUMN (eval_rule_id INTEGER);
ALTER TABLE test_case ADD COLUMN (date_set_code VARCHAR(120) NOT NULL DEFAULT 'F');

CREATE TABLE date_set
(
  date_set_code  VARCHAR(120) NOT NULL PRIMARY KEY,
  label           VARCHAR(120) NOT NULL
);

INSERT INTO date_set (date_set_code, label) VALUES ('R', 'Relative');
INSERT INTO date_set (date_set_code, label) VALUES ('F', 'Fixed');

ALTER TABLE test_event ADD COLUMN (event_rule_id INTEGER);

INSERT INTO event_type(event_type_code, label) VALUES('B', 'Birth');
INSERT INTO event_type(event_type_code, label) VALUES('A', 'ACIP Defined Condition');
INSERT INTO event_type(event_type_code, label) VALUES('N', 'Condition Implication');

CREATE TABLE associated_date
(
  associated_date_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_event_id        INTEGER NOT NULL,
  date_value           DATE NOT NULL,
  date_rule_id         INTEGER,
  date_type_code       VARCHAR(120) NOT NULL
);

CREATE TABLE date_type
(
  date_type_code  VARCHAR(120) NOT NULL PRIMARY KEY,
  label           VARCHAR(120) NOT NULL
);

INSERT INTO date_type (date_type_code, label) VALUES ('STA', 'Start Date');
INSERT INTO date_type (date_type_code, label) VALUES ('ONS', 'Onset Date');
INSERT INTO date_type (date_type_code, label) VALUES ('EFF', 'Effective Date');
INSERT INTO date_type (date_type_code, label) VALUES ('EXP', 'Expected Date');
INSERT INTO date_type (date_type_code, label) VALUES ('END', 'End Date');
INSERT INTO date_type (date_type_code, label) VALUES ('RES', 'Resolution Date');
INSERT INTO date_type (date_type_code, label) VALUES ('OBS', 'Observation Date');

-- Page 3

ALTER TABLE forecast_expected ADD COLUMN (valid_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (due_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (overdue_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (finished_rule_id INTEGER);


ALTER TABLE evaluation_expected ADD COLUMN (evaluation_reason_code VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (vaccine_cvx            VARCHAR(20));
ALTER TABLE evaluation_expected ADD COLUMN (series_used_code       VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (series_used_text       VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (dose_number            VARCHAR(20));

-- Page 4

CREATE TABLE test_panel_guidance (
  test_panel_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_panel_case_id      INTEGER NOT NULL,
  guidance_evaluation_id  INTEGER NOT NULL
);

CREATE TABLE guidance_expected (
  guidance_expected_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_id             INTEGER NOT NULL,
  test_case_id            INTEGER NOT NULL,
  author_user_id          INTEGER NOT NULL,
  updated_date            DATETIME NOT NULL,
  effective_rule_id       INTEGER,
  expiration_rule_id      INTEGER
);

CREATE TABLE guidance_actual (
  guidance_actual_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_result_id      INTEGER NOT NULL,
  guidance_id             INTEGER NOT NULL
);

CREATE TABLE guidance (
  guidance_id             INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  vaccine_group_id        INTEGER NOT NULL,
  effective_date          DATE,
  expiration_date         DATE
);

CREATE TABLE recommend_guidance (
  recommend_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  recommend_id           INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE consideration_guidance (
  consideration_guidance_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  consideration_id          INTEGER NOT NULL,
  guidance_id               INTEGER NOT NULL
);

CREATE TABLE rationale_guidance (
  rationale_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rationale_id           INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE resource_guidance (
  resource_guidance_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  resource_id            INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE recommend (
  recommend_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  recommend_text         VARCHAR(2500) NOT NULL,
  recommend_type_code    VARCHAR(5) NOT NULL,
  recommend_range_code   VARCHAR(5) NOT NULL
);

CREATE TABLE recommend_type (
  recommend_type_code    VARCHAR(5) NOT NULL PRIMARY KEY,
  label                  VARCHAR(120) NOT NULL
);

INSERT INTO recommend_type (recommend_type_code, label) VALUES ('C', 'Contraindication'); 
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('P', 'Precaution');
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('D', 'Disease');
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('I', 'Indication');

CREATE TABLE recommend_range
(
  recommend_range_code   VARCHAR(5) NOT NULL PRIMARY KEY,
  label                  VARCHAR(120) NOT NULL
);

INSERT INTO recommend_range (recommend_range_code, label) VALUES ('T', 'Temporal');
INSERT INTO recommend_range (recommend_range_code, label) VALUES ('P', 'Permanent');

CREATE TABLE consideration (
  consideration_id        INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  consideration_text      VARCHAR(2500) NOT NULL,
  consideration_type_code VARCHAR(5) NOT NULL 
);

CREATE TABLE consideration_type (
  consideration_type_code   VARCHAR(5) NOT NULL PRIMARY KEY,
  label                     VARCHAR(120) NOT NULL
);

INSERT INTO consideration_type (consideration_type_code, label) VALUES ('S', 'Specific');
INSERT INTO consideration_type (consideration_type_code, label) VALUES ('G', 'General');

CREATE TABLE rationale (
  rationale_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rationale_text         VARCHAR(2500) NOT NULL
);

CREATE TABLE resource (
  resource_id            INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  resource_text          VARCHAR(2500) NOT NULL,
  resource_link          VARCHAR(2500) NOT NULL
);

-- page 5

CREATE TABLE guidance_actual_rating (
  guidance_actual_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_actual_id        INTEGER NOT NULL,
  expert_rating_id          INTEGER NOT NULL
);


CREATE TABLE guidance_expected_rating (
  guidance_expected_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_expected_id        INTEGER NOT NULL,
  expert_rating_id           INTEGER NOT NULL
);

-- page 6

ALTER TABLE software ADD COLUMN (supports_fixed VARCHAR(1) NOT NULL DEFAULT 'N');

UPDATE software SET supports_fixed = 'Y' where software_id = 2 or software_id = 5 or software_id = 6 or software_id = 7 or software_id = 8 or software_id = 10 or software_id = 12;

