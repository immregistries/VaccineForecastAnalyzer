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