
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

