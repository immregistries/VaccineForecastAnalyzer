INSERT INTO software (software_id, label, service_url, service_type, visible_status) VALUES (10, 'ICE', 'http://localhost:8086/o/evaluate', 'ice', 'R');


INSERT INTO task_group (label, primary_software_id) values ('ICE User Group', 10);

INSERT INTO expert (user_id, task_group_id, role_status) values (2, 7, 'E');