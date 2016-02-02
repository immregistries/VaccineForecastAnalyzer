INSERT INTO vaccine_group (vaccine_group_id, label) VALUES (35, 'Mening Bexsero');
INSERT INTO vaccine_group (vaccine_group_id, label) VALUES (36, 'Mening Trumenba');
INSERT INTO vaccine_group (vaccine_group_id, label) VALUES (37, 'MeningB');

UPDATE vaccine_group SET map_to_cdsi_code = 'MCV' WHERE label = 'Mening Bexsero';
UPDATE vaccine_group SET map_to_cdsi_code = 'MCV' WHERE label = 'Mening Trumenba';
UPDATE vaccine_group SET map_to_cdsi_code = 'MCV' WHERE label = 'MeningB';
