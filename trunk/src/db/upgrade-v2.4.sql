
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

