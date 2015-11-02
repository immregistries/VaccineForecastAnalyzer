ALTER TABLE vaccine_group ADD COLUMN map_to_cdsi_code VARCHAR(200);

UPDATE vaccine_group SET map_to_cdsi_code = 'DTaP' WHERE label = 'DTaP';
UPDATE vaccine_group SET map_to_cdsi_code = 'DTaP' WHERE label = 'Tdap or Td';
UPDATE vaccine_group SET map_to_cdsi_code = 'DTaP' WHERE label = 'Td Only';
UPDATE vaccine_group SET map_to_cdsi_code = 'DTaP' WHERE label = 'Tdap Only';
UPDATE vaccine_group SET map_to_cdsi_code = 'DTaP' WHERE label = 'DTaP, Tdap or Td';

UPDATE vaccine_group SET map_to_cdsi_code = 'Flu' WHERE label = 'Influenza';
UPDATE vaccine_group SET map_to_cdsi_code = 'Flu' WHERE label = 'Influenza IIV';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Influenza LAIV';

UPDATE vaccine_group SET map_to_cdsi_code = 'HepA' WHERE label = 'Hep A';

UPDATE vaccine_group SET map_to_cdsi_code = 'HepB' WHERE label = 'Hep B';
UPDATE vaccine_group SET map_to_cdsi_code = 'HepB' WHERE label = 'Hep B 2 Dose Only';
UPDATE vaccine_group SET map_to_cdsi_code = 'HepB' WHERE label = 'Hep B 3 Dose Only';

UPDATE vaccine_group SET map_to_cdsi_code = 'Hib' WHERE label = 'Hib';

UPDATE vaccine_group SET map_to_cdsi_code = 'HPV' WHERE label = 'HPV';

UPDATE vaccine_group SET map_to_cdsi_code = 'MCV' WHERE label = 'Meningococcal';

UPDATE vaccine_group SET map_to_cdsi_code = 'MMR' WHERE label = 'MMR';

UPDATE vaccine_group SET map_to_cdsi_code = 'PCV' WHERE label = 'Pneumococcal';
UPDATE vaccine_group SET map_to_cdsi_code = 'PCV' WHERE label = 'PCV';

UPDATE vaccine_group SET map_to_cdsi_code = 'POL' WHERE label = 'Polio';

UPDATE vaccine_group SET map_to_cdsi_code = 'Rota' WHERE label = 'Rotavirus';

UPDATE vaccine_group SET map_to_cdsi_code = 'Var' WHERE label = 'Varicella';

-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'HerpesZoster';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'PPSV';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Measles Only';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Mumps Only';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Rubella Only';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Anthrax';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Smallpox Shot/Reading';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Novel H1N1';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Typhoid';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Japanese Encephalitis';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Rabies';
-- UPDATE vaccine_group SET map_to_cdsi_code = '' WHERE label = 'Yellow Fever';


INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (160, 'Influenza A monovalent (H5N1), ADJUVANTED-2013', 'V', '160', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (161, 'Influenza, injectable,quadrivalent, preservative free, pediatric', 'V', '161', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (162, 'meningococcal B, recombinant', 'V', '162', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (163, 'meningococcal B, OMV	', 'V', '163', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (164, 'meningococcal B, unspecified', 'V', '164', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (165, 'HPV9', 'V', '165', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (166, 'influenza, intradermal, quadrivalent, preservative free', 'V', '166', '');
INSERT INTO event (event_id, label, event_type_code, vaccine_cvx, vaccine_mvx) VALUES (801, 'AS03 Adjuvant', 'V', '801', '');

