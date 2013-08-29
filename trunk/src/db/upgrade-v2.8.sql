INSERT INTO software (software_id, label, service_url, service_type) VALUES (12, 'ICE Forecaster', 'http://http://tchforecasttester.org/ice/evaluate', 'ice');

INSERT INTO task_group (task_group_id, label, primary_software_id) VALUES (12, 'ICE User Group', 12);

INSERT INTO expert (user_id, task_group_id, role_status) VALUES (2, 12, 'E'); -- Adding nathan to ICE User Group
