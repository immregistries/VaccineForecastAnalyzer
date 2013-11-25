INSERT INTO software (software_id, label, service_url, service_type, visible_status) VALUES (14, 'DQA', '', 'hl7', 'V');

insert into forecast_item (forecast_item_id, label) values (30, 'Japanese Encephalitis');
insert into forecast_item (forecast_item_id, label) values (31, 'Rabies');
insert into forecast_item (forecast_item_id, label) values (32, 'Yellow Fever');


CREATE TABLE forecast_cvx
(
  forecast_cvx_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  forecast_item_id  INTEGER NOT NULL,
  vaccine_cvx       VARCHAR(20)
);

INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (26, "24");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "20");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "20");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "110");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "110");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "110");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (11, "110");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "120");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "120");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (6, "120");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (11, "120");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "130");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "130");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (11, "130");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "106");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "106");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (2, "107");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "107");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (4, "104");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "104");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (4, "52");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (4, "83");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (4, "85");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "8");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "43");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "44");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "45");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (6, "49");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (6, "48");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (6, "51");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (5, "51");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (6, "17");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (7, "118");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (7, "62");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (7, "137");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "151");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "135");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "153");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "158");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "150");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "111");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "149");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "155");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "141");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "140");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "144");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (3, "88");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (11, "10");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "148");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "147");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "136");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "114");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "32");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (8, "108");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (9, "3");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (9, "94");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (13, "94");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (28, "128");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (17, "133");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (10, "133");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (17, "152");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (10, "152");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (16, "33");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (10, "33");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (10, "109");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (11, "89");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (12, "119");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (12, "116");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (12, "122");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (18, "138");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (15, "138");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "138");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (18, "113");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (15, "113");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "113");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (18, "9");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (15, "9");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "9");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (18, "139");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (15, "139");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "139");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (25, "115");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (15, "115");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (19, "115");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (29, "25");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (29, "41");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (29, "53");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (29, "91");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (29, "101");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (27, "75");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (13, "21");
INSERT INTO forecast_cvx (forecast_item_id, vaccine_cvx) VALUES (14, "121");
