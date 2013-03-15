ALTER TABLE software ADD COLUMN (visible_status VARCHAR(1) NOT NULL DEFAULT 'V');

INSERT INTO software (software_id, label, service_url, service_type, visible_status) VALUES (9, 'STC Forecaster', 'http://epicenter.stchome.com/safdemo/soa/forecast/getForecast.wsdl', 'stc', 'R');

CREATE TABLE software_compare (
  software_compare_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_id          INTEGER NOT NULL,
  test_panel_id        INTEGER NOT NULL
);

CREATE TABLE software_target (
  software_target_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_compare_id  INTEGER NOT NULL,
  software_id          INTEGER NOT NULL
);

CREATE TABLE forecast_compare (
  forecast_compare_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_compare_id  INTEGER NOT NULL,
  forecast_actual_id   INTEGER NOT NULL,
  compare_label        VARCHAR(120) NOT NULL,
  result_status        VARCHAR(1)
);

CREATE TABLE forecast_target (
  forecast_target_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_compare_id  INTEGER NOT NULL,
  forecast_actual_id   INTEGER NOT NULL
);

ALTER TABLE user ADD COLUMN (selected_software_compare_id INTEGER);