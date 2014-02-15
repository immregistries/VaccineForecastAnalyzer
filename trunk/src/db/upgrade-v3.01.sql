ALTER TABLE test_case ADD COLUMN (evaluation_type_id INTEGER);
ALTER TABLE test_case ADD COLUMN (forecast_type_id INTEGER);

CREATE TABLE evaluation_type (
  evaluation_type_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label                VARCHAR(120)
);

INSERT INTO evaluation_type (label) VALUES ('Age: At Absolute Minimum');
INSERT INTO evaluation_type (label) VALUES ('Age: At Minimum');
INSERT INTO evaluation_type (label) VALUES ('Age: At Recommended');
INSERT INTO evaluation_type (label) VALUES ('Age: Below Absolute Minimum');
INSERT INTO evaluation_type (label) VALUES ('Age: Too Old');
INSERT INTO evaluation_type (label) VALUES ('All Valid: Forecast Test');
INSERT INTO evaluation_type (label) VALUES ('Extra Doses');
INSERT INTO evaluation_type (label) VALUES ('Gender: Invalid Administration');
INSERT INTO evaluation_type (label) VALUES ('Interval: At Absolute Minimum');
INSERT INTO evaluation_type (label) VALUES ('Interval: At Minimum');
INSERT INTO evaluation_type (label) VALUES ('Interval: At Recommended');
INSERT INTO evaluation_type (label) VALUES ('Interval: Below Absolute Minimum');
INSERT INTO evaluation_type (label) VALUES ('Live Virus: At Minimum');
INSERT INTO evaluation_type (label) VALUES ('Live Virus: Below Minimum');
INSERT INTO evaluation_type (label) VALUES ('No Doses Administered');
INSERT INTO evaluation_type (label) VALUES ('Single Antigen Administration');
INSERT INTO evaluation_type (label) VALUES ('Vaccine: Invalid Usage');
INSERT INTO evaluation_type (label) VALUES ('Vaccine: Off Label');

CREATE TABLE forecast_type (
  forecast_type_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  label              VARCHAR(120)
);

INSERT INTO forecast_type (label) VALUES ('Not recommended: contraindication');
INSERT INTO forecast_type (label) VALUES ('Not recommended: immune');
INSERT INTO forecast_type (label) VALUES ('Not recommended: series complete');
INSERT INTO forecast_type (label) VALUES ('Not recommended: too old');
INSERT INTO forecast_type (label) VALUES ('Recommended based on age');
INSERT INTO forecast_type (label) VALUES ('Recommended based on interval');
INSERT INTO forecast_type (label) VALUES ('Recommended based on minimum interval from invalid dose');
INSERT INTO forecast_type (label) VALUES ('Recommended based on minimum interval from live virus vaccine');
INSERT INTO forecast_type (label) VALUES ('Recommended based on minimum interval from previous dose (catch-up)');
INSERT INTO forecast_type (label) VALUES ('Recommended based on seasonal start date');

ALTER TABLE event ADD COLUMN (trade_label VARCHAR(120));

CREATE TABLE software_result (
  software_result_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_id          INTEGER NOT NULL,
  run_date             DATETIME NOT NULL,
  log_text             MEDIUMTEXT,
  test_case_id         INTEGER NOT NULL
) SELECT forecast_actual_id, software_id, run_date, log_text, test_case_id FROM forecast_actual;

ALTER TABLE forecast_actual ADD COLUMN (software_result_id INTEGER);
UPDATE forecast_actual SET software_result_id = forecast_actual_id;
ALTER TABLE forecast_actual CHANGE software_result_id software_result_id INTEGER NOT NULL;
ALTER TABLE forecast_actual DROP COLUMN software_id;
ALTER TABLE forecast_actual DROP COLUMN run_date;
ALTER TABLE forecast_actual DROP COLUMN log_text;
ALTER TABLE forecast_actual DROP COLUMN test_case_id;
ALTER TABLE forecast_actual CHANGE forecast_item_id vaccine_group_id INTEGER NOT NULL;
ALTER TABLE forecast_actual ADD COLUMN admin_status VARCHAR(1);
ALTER TABLE forecast_actual ADD COLUMN forecast_reason VARCHAR(120);

CREATE TABLE admin (
  admin_status    VARCHAR(1) NOT NULL PRIMARY KEY,
  label           VARCHAR(120)
);
INSERT INTO admin(admin_status, label) VALUES ('D', 'due' );
INSERT INTO admin(admin_status, label) VALUES ('O', 'overdue' );
INSERT INTO admin(admin_status, label) VALUES ('L', 'due later' );
INSERT INTO admin(admin_status, label) VALUES ('X', 'contraindicated' );
INSERT INTO admin(admin_status, label) VALUES ('C', 'complete' );
INSERT INTO admin(admin_status, label) VALUES ('S', 'complete for season' );
INSERT INTO admin(admin_status, label) VALUES ('F', 'finished' );
INSERT INTO admin(admin_status, label) VALUES ('N', 'not complete');
INSERT INTO admin(admin_status, label) VALUES ('I', 'immune' );
INSERT INTO admin(admin_status, label) VALUES ('U', 'unknown' );
INSERT INTO admin(admin_status, label) VALUES ('E', 'error' );
INSERT INTO admin(admin_status, label) VALUES ('R', 'no results' );

CREATE TABLE evaluation_actual (
  evaluation_actual_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_result_id     INTEGER NOT NULL,
  test_event_id          INTEGER NOT NULL,
  evaluation_status      VARCHAR(1),
  evaluation_reason      VARCHAR(120),
  evaluation_reason_code VARCHAR(120),
  vaccine_group_id       INTEGER NOT NULL,
  vaccine_cvx            VARCHAR(20),
  series_used_code       VARCHAR(120),
  series_used_text       VARCHAR(120),
  dose_number            VARCHAR(20)
);

CREATE TABLE evaluation (
  evaluation_status VARCHAR(1) NOT NULL PRIMARY KEY,
  label             VARCHAR(120)
);
INSERT INTO evaluation (evaluation_status, label) VALUES ('', '');
INSERT INTO evaluation (evaluation_status, label) VALUES ('E', 'extraneous');
INSERT INTO evaluation (evaluation_status, label) VALUES ('N', 'not valid');
INSERT INTO evaluation (evaluation_status, label) VALUES ('V', 'valid');
INSERT INTO evaluation (evaluation_status, label) VALUES ('S', 'sub-standard');

CREATE TABLE test_panel_evaluation (
  test_panel_evaluation_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_panel_case_id       INTEGER NOT NULL,
  forecast_evaluation_id   INTEGER NOT NULL
);

CREATE TABLE evaluation_expected (
  evaluation_expected_id     INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_case_id               INTEGER NOT NULL,
  author_user_id             INTEGER NOT NULL,
  updated_date               DATETIME NOT NULL,
  test_event_id              INTEGER NOT NULL,
  evaluation_status          VARCHAR(1),
  evaluation_reason          VARCHAR(120),
  vaccine_group_id           INTEGER NOT NULL
);

RENAME TABLE test_panel_expected TO test_panel_forecast;
ALTER TABLE test_panel_forecast CHANGE test_panel_expected_id test_panel_forecast_id INTEGER NOT NULL;

RENAME TABLE forecast_item TO vaccine_group;
ALTER TABLE vaccine_group CHANGE forecast_item_id vaccine_group_id INTEGER NOT NULL AUTO_INCREMENT;

ALTER TABLE forecast_expected ADD COLUMN (updated_date DATETIME); 
UPDATE forecast_expected SET updated_date = NOW();
ALTER TABLE forecast_expected CHANGE updated_date updated_date DATETIME NOT NULL;
ALTER TABLE forecast_expected CHANGE forecast_item_id vaccine_group_id INTEGER NOT NULL;
ALTER TABLE forecast_expected ADD COLUMN admin_status VARCHAR(1);
ALTER TABLE forecast_expected ADD COLUMN forecast_reason VARCHAR(120);

ALTER TABLE forecast_cvx CHANGE forecast_item_id vaccine_group_id INTEGER NOT NULL;

CREATE TABLE evaluation_expected_rating (
  evaluation_expected_rating_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evaluation_expected_id         INTEGER NOT NULL,
  expert_rating_id               INTEGER NOT NULL
);

CREATE TABLE evaluation_actual_rating (
  evaluation_actual_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evaluation_actual_id        INTEGER NOT NULL,
  expert_rating_id            INTEGER NOT NULL
);

CREATE TABLE forecast_expected_rating (
  forecast_expected_rating_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_expected_id         INTEGER NOT NULL,
  expert_rating_id             INTEGER NOT NULL
) SELECT NULL, forecast_expected_id, expert_rating_id FROM expert_rating;

CREATE TABLE forecast_actual_rating (
  forecast_actual_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_actual_id        INTEGER NOT NULL,
  expert_rating_id          INTEGER NOT NULL
);

ALTER TABLE expert_rating DROP COLUMN forecast_expected_id;

INSERT INTO rating(rating_status, label) VALUES ('L', 'Like');
INSERT INTO rating(rating_status, label) VALUES ('C', 'Comment');
INSERT INTO rating(rating_status, label) VALUES ('P', 'Problem');

CREATE TABLE test_case_rating (
  test_case_rating_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  expert_rating_id         INTEGER NOT NULL,
  test_case_id             INTEGER NOT NULL
);

CREATE TABLE evaluation_compare (
  evaluation_compare_id     INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_compare_id       INTEGER NOT NULL,
  evaluation_actual_id      INTEGER NOT NULL,
  compare_label             VARCHAR(120) NOT NULL,
  result_status             VARCHAR(1)  
);

CREATE TABLE evaluation_target (
  evaluation_target_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  evaluation_compare_id     INTEGER NOT NULL,
  evaluation_actual_id      INTEGER NOT NULL
);

INSERT INTO vaccine_group (vaccine_group_id, label) VALUES (33, 'Influenza LAIV');
INSERT INTO vaccine_group (vaccine_group_id, label) VALUES (34, 'Influenza IIV');

UPDATE forecast_actual SET admin_status = 'C', dose_number = null WHERE dose_number = 'COMP';

UPDATE forecast_expected SET admin_status = 'C', dose_number = null WHERE dose_number = 'COMP';

UPDATE forecast_expected SET admin_status = 'S' WHERE admin_status = 'C' AND vaccine_group_id = 3;


UPDATE forecast_expected fe JOIN test_case tc on (fe.test_case_id = tc.test_case_id) 
SET fe.admin_status = 'D' 
WHERE (fe.admin_status IS NULL || fe.admin_status = '') 
  AND fe.due_date <= tc.eval_date 
  AND fe.overdue_date > tc.eval_date;
  
UPDATE forecast_expected fe JOIN test_case tc on (fe.test_case_id = tc.test_case_id) 
SET fe.admin_status = 'O' 
WHERE (fe.admin_status IS NULL || fe.admin_status = '') 
  AND fe.overdue_date <= tc.eval_date;
  
UPDATE forecast_expected fe JOIN test_case tc on (fe.test_case_id = tc.test_case_id) 
SET fe.admin_status = 'L' 
WHERE (fe.admin_status IS NULL || fe.admin_status = '') 
  AND fe.due_date > tc.eval_date;