
update forecast_item set label = 'Hep A' where forecast_item_id = 4;
update forecast_item set label = 'Hep B' where forecast_item_id = 5;
update forecast_item set label = 'Tdap or Td' where forecast_item_id = 15;

insert into forecast_item (forecast_item_id, label) values (16, 'PPSV');
insert into forecast_item (forecast_item_id, label) values (17, 'PCV');
insert into forecast_item (forecast_item_id, label) values (18, 'Td Only');
insert into forecast_item (forecast_item_id, label) values (19, 'DTaP, Tdap or Td');
insert into forecast_item (forecast_item_id, label) values (20, 'Hep B 2 Dose Only');
insert into forecast_item (forecast_item_id, label) values (21, 'Hep B 3 Dose Only');
insert into forecast_item (forecast_item_id, label) values (22, 'Measles Only');
insert into forecast_item (forecast_item_id, label) values (23, 'Mumps Only');
insert into forecast_item (forecast_item_id, label) values (24, 'Rubella Only');
insert into forecast_item (forecast_item_id, label) values (25, 'Tdap Only');

CREATE TABLE software_setting
(
  setting_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_id          INTEGER NOT NULL,
  option_id            INTEGER NOT NULL,
  option_value         VARCHAR(120) NOT NULL
);

CREATE TABLE test_case_setting
(
  setting_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_case_id         INTEGER NOT NULL,
  option_id            INTEGER NOT NULL,
  option_value         VARCHAR(120) NOT NULL
);

CREATE TABLE service_option
(
  option_id            INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  service_type         VARCHAR(10) NOT NULL,
  option_name          VARCHAR(120) NOT NULL,
  option_label         VARCHAR(120) NOT NULL,
  description          VARCHAR(4000),
  valid_values         VARCHAR(1000),
  base_option_id       INTEGER
);

INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (2, "base", "fluSeasonStart", "Flu Season Start", "The number of months after the influenza season ends that the season begins.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (3, "tch", "fluSeasonStart", "Flu Season Start", "The number of months after the influenza season ends that the season begins.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", 2);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (4, "base", "fluSeasonDue", "Flu Season Due", "The number of months after the season starts that the influenza vaccine is due to be given.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (5, "tch", "fluSeasonDue", "Flu Season Due", "The number of months after the season starts that the influenza vaccine is due to be given.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", 4);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (6, "base", "fluSeasonOverdue", "Flu Season Overdue", "The number of months after the season starts that the influenza vaccine is over due.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (7, "tch", "fluSeasonOverdue", "Flu Season Overdue", "The number of months after the season starts that the influenza vaccine is over due.", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", 6);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (8, "base", "fluSeasonEnd", "Flu Season End", "The number of months from January 1st of every year until the start of the next influenza vaccination season. For example, 6 months indicates a July 1st start to the season. ", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (9, "tch", "fluSeasonEnd", "Flu Season End", "The number of months from January 1st of every year until the start of the next influenza vaccination season. For example, 6 months indicates a July 1st start to the season. ", "0 months, 1 month, 2 months, 3 months, 4 months, 5 months, 6 months, 7 months, 8 months", 8);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (10, "base", "useEarlyDue", "Use Early Due", "Forecaster should use earliest possible due date when making due recommendations. ", "True, False", NULL);
INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (11, "tch", "useEarlyDue", "Use Early Due", "Forecaster should use earliest possible due date when making due recommendations. ", "True, False", 10);

INSERT INTO service_option (option_id, service_type, option_name, option_label, description, valid_values, base_option_id) VALUES (10000, "base", "PLACEHOLDER", "Placeholder", "This is a placeholder record not a real option", "", NULL);

INSERT INTO software (software_id, label, service_url, service_type) VALUES (10, 'TCH Forecast for IHS', 'http://tchforecasttester.org/fv/forecast', 'tch');
