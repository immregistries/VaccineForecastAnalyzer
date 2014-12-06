
-- Page 2

CREATE TABLE relative_rule
(
  rule_id          INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  time_period      VARCHAR(500) NOT NULL,
  relative_to_code VARCHAR(1) NOT NULL DEFAULT 'B',
  test_event_id    INTEGER, 
  and_rule_id      INTEGER
);

CREATE TABLE relative_to
(
  relative_to_code  VARCHAR(1) NOT NULL PRIMARY KEY,
  label             VARCHAR(250) NOT NULL
);

INSERT INTO relative_to (relative_to_code, label) VALUES ('B', 'Birth');
INSERT INTO relative_to (relative_to_code, label) VALUES ('E', 'Event');
INSERT INTO relative_to (relative_to_code, label) VALUES ('L', 'Evaluation');

ALTER TABLE test_case ADD COLUMN (eval_rule_id INTEGER);
ALTER TABLE test_case ADD COLUMN (date_set_code VARCHAR(120) NOT NULL DEFAULT 'F');
ALTER TABLE test_case ADD COLUMN (vaccine_group_id INTEGER);

CREATE TABLE date_set
(
  date_set_code  VARCHAR(120) NOT NULL PRIMARY KEY,
  label           VARCHAR(120) NOT NULL
);

INSERT INTO date_set (date_set_code, label) VALUES ('R', 'Relative');
INSERT INTO date_set (date_set_code, label) VALUES ('F', 'Fixed');

ALTER TABLE test_event ADD COLUMN (event_rule_id INTEGER);

INSERT INTO event_type(event_type_code, label) VALUES('B', 'Birth');
INSERT INTO event_type(event_type_code, label) VALUES('A', 'ACIP Defined Condition');
INSERT INTO event_type(event_type_code, label) VALUES('N', 'Condition Implication');
INSERT INTO event_type(event_type_code, label) VALUES('L', 'Evaluation');

CREATE TABLE associated_date
(
  associated_date_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_event_id        INTEGER NOT NULL,
  date_value           DATE NOT NULL,
  date_rule_id         INTEGER,
  date_type_code       VARCHAR(120) NOT NULL
);

CREATE TABLE date_type
(
  date_type_code  VARCHAR(120) NOT NULL PRIMARY KEY,
  label           VARCHAR(120) NOT NULL
);

INSERT INTO date_type (date_type_code, label) VALUES ('STA', 'Start Date');
INSERT INTO date_type (date_type_code, label) VALUES ('ONS', 'Onset Date');
INSERT INTO date_type (date_type_code, label) VALUES ('EFF', 'Effective Date');
INSERT INTO date_type (date_type_code, label) VALUES ('EXP', 'Expected Date');
INSERT INTO date_type (date_type_code, label) VALUES ('END', 'End Date');
INSERT INTO date_type (date_type_code, label) VALUES ('RES', 'Resolution Date');
INSERT INTO date_type (date_type_code, label) VALUES ('OBS', 'Observation Date');

-- Page 3

ALTER TABLE forecast_expected ADD COLUMN (valid_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (due_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (overdue_rule_id INTEGER);
ALTER TABLE forecast_expected ADD COLUMN (finished_rule_id INTEGER);

ALTER TABLE evaluation_expected ADD COLUMN (evaluation_reason_code VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (vaccine_cvx            VARCHAR(20));
ALTER TABLE evaluation_expected ADD COLUMN (series_used_code       VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (series_used_text       VARCHAR(120));
ALTER TABLE evaluation_expected ADD COLUMN (dose_number            VARCHAR(20));

ALTER TABLE test_panel_evaluation CHANGE COLUMN forecast_evaluation_id evaluation_expected_id INTEGER NOT NULL;

INSERT INTO admin (admin_status, label) VALUES ('G', 'aged out');

-- Page 4

CREATE TABLE test_panel_guidance (
  test_panel_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  test_panel_case_id      INTEGER NOT NULL,
  guidance_expected_id  INTEGER NOT NULL
);

CREATE TABLE guidance_expected (
  guidance_expected_id    INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_id             INTEGER NOT NULL,
  test_case_id            INTEGER NOT NULL,
  author_user_id          INTEGER NOT NULL,
  updated_date            DATETIME NOT NULL,
  effective_rule_id       INTEGER,
  expiration_rule_id      INTEGER
);

CREATE TABLE guidance_actual (
  guidance_actual_id      INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  software_result_id      INTEGER NOT NULL,
  guidance_id             INTEGER NOT NULL
);

CREATE TABLE guidance (
  guidance_id             INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  vaccine_group_id        INTEGER NOT NULL,
  effective_date          DATE,
  expiration_date         DATE
);

CREATE TABLE recommend_guidance (
  recommend_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  recommend_id           INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE consideration_guidance (
  consideration_guidance_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  consideration_id          INTEGER NOT NULL,
  guidance_id               INTEGER NOT NULL
);

CREATE TABLE rationale_guidance (
  rationale_guidance_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rationale_id           INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE resource_guidance (
  resource_guidance_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  resource_id            INTEGER NOT NULL,
  guidance_id            INTEGER NOT NULL
);

CREATE TABLE recommend (
  recommend_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  recommend_text         VARCHAR(2500) NOT NULL,
  recommend_type_code    VARCHAR(5),
  recommend_range_code   VARCHAR(5)
);

CREATE TABLE recommend_type (
  recommend_type_code    VARCHAR(5) NOT NULL PRIMARY KEY,
  label                  VARCHAR(120) NOT NULL
);

INSERT INTO recommend_type (recommend_type_code, label) VALUES ('C', 'Contraindication'); 
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('P', 'Precaution');
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('D', 'Disease');
INSERT INTO recommend_type (recommend_type_code, label) VALUES ('I', 'Indication');

CREATE TABLE recommend_range
(
  recommend_range_code   VARCHAR(5) NOT NULL PRIMARY KEY,
  label                  VARCHAR(120) NOT NULL
);

INSERT INTO recommend_range (recommend_range_code, label) VALUES ('T', 'Temporal');
INSERT INTO recommend_range (recommend_range_code, label) VALUES ('P', 'Permanent');

CREATE TABLE consideration (
  consideration_id        INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  consideration_text      VARCHAR(2500) NOT NULL,
  consideration_type_code VARCHAR(5) 
);

CREATE TABLE consideration_type (
  consideration_type_code   VARCHAR(5) NOT NULL PRIMARY KEY,
  label                     VARCHAR(120) NOT NULL
);

INSERT INTO consideration_type (consideration_type_code, label) VALUES ('S', 'Specific');
INSERT INTO consideration_type (consideration_type_code, label) VALUES ('G', 'General');

CREATE TABLE rationale (
  rationale_id           INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  rationale_text         VARCHAR(2500) NOT NULL
);

CREATE TABLE resource (
  resource_id            INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  resource_text          VARCHAR(2500) NOT NULL,
  resource_link          VARCHAR(2500) NOT NULL
);

-- page 5

CREATE TABLE guidance_actual_rating (
  guidance_actual_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_actual_id        INTEGER NOT NULL,
  expert_rating_id          INTEGER NOT NULL
);


CREATE TABLE guidance_expected_rating (
  guidance_expected_rating_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  guidance_expected_id        INTEGER NOT NULL,
  expert_rating_id           INTEGER NOT NULL
);

-- page 6

ALTER TABLE software ADD COLUMN (supports_fixed VARCHAR(1) NOT NULL DEFAULT 'N');

UPDATE software SET supports_fixed = 'Y' where software_id = 2 or software_id = 5 or software_id = 6 or software_id = 7 or software_id = 8 or software_id = 10 or software_id = 12;



-- data 

ALTER TABLE event CHANGE COLUMN label label VARCHAR(1200);

INSERT INTO event (label, event_type_code) values ('Acquired asplenia', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in correctional facilities', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in end-stage renal disease programs and facilities for chronic hemodialysis patients', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in facilities providing drug-abuse treatment and prevention services', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in health-care settings targeting services men who have sex with men', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in health-care settings targeting services to injection-drug users', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in HIV testing and treatment facilities', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in institutions for persons with developmental disabilities', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in nonresidential daycare facilities for persons with developmental disabilities', 'A');
INSERT INTO event (label, event_type_code) values ('Adults in STD treatment facilities', 'A');
INSERT INTO event (label, event_type_code) values ('Adults who are students in postsecondary educational institutions', 'A');
INSERT INTO event (label, event_type_code) values ('Adults who plan to travel internationally', 'A');
INSERT INTO event (label, event_type_code) values ('Adults who smoke cigarettes', 'A');
INSERT INTO event (label, event_type_code) values ('Adults who work in a health-care facility', 'A');
INSERT INTO event (label, event_type_code) values ('Alcoholism', 'A');
INSERT INTO event (label, event_type_code) values ('Anatomic asplenia', 'A');
INSERT INTO event (label, event_type_code) values ('Asplenia (including elective splenectomy and persistent complement component deficiencies)', 'A');
INSERT INTO event (label, event_type_code) values ('Asthma', 'A');
INSERT INTO event (label, event_type_code) values ('Asymptomatic HIV infection', 'A');
INSERT INTO event (label, event_type_code) values ('Cerebrospinal fluid leaks (CSF Leaks)', 'A');
INSERT INTO event (label, event_type_code) values ('Chonic liver disease', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic Alcoholism', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic cardiovascular dieseases', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic liver disease', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic lung Disease', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic obstrictive pulmonary disease', 'A');
INSERT INTO event (label, event_type_code) values ('Chronic renal failure', 'A');
INSERT INTO event (label, event_type_code) values ('Cirrhosis', 'A');
INSERT INTO event (label, event_type_code) values ('Clients of institutions for persons with developmental disabilities', 'A');
INSERT INTO event (label, event_type_code) values ('Cochlear implants', 'A');
INSERT INTO event (label, event_type_code) values ('Congenital asplenia', 'A');
INSERT INTO event (label, event_type_code) values ('Current or recent injection-drug users', 'A');
INSERT INTO event (label, event_type_code) values ('Diabetes mellitus', 'A');
INSERT INTO event (label, event_type_code) values ('Elective splenectomy', 'A');
INSERT INTO event (label, event_type_code) values ('Emphysema', 'A');
INSERT INTO event (label, event_type_code) values ('Encephalopathy (e.g., coma, decreased level of consciousness, or prolonged seizures) not attributable to another identifiable cause within 7 days of administration of a previous dose of Tdap or diphtheria and tetanus toxoids and pertussis (DTP) or diphtheria and tetanus toxoids and acellular pertussis (DTaP) vaccine', 'A');
INSERT INTO event (label, event_type_code) values ('End-stage renal disease', 'A');
INSERT INTO event (label, event_type_code) values ('Functional asplenia', 'A');
INSERT INTO event (label, event_type_code) values ('Healthcare personnel', 'A');
INSERT INTO event (label, event_type_code) values ('Health-care personnel who are potentially exposed to blood', 'A');
INSERT INTO event (label, event_type_code) values ('Health-care personnel who are potentially exposed to infectious body fluids', 'A');
INSERT INTO event (label, event_type_code) values ('Health-care personnel who care for severely immunocompromised persons (i.e., those who require care in a protected environment)', 'A');
INSERT INTO event (label, event_type_code) values ('Heart Disease', 'A');
INSERT INTO event (label, event_type_code) values ('History of arthus-type hypersensitivity reactions after a previous dose of tetanus or diptheria toxoid–containing vaccine', 'A');
INSERT INTO event (label, event_type_code) values ('History of Guillain-Barré Syndrome (GBS) within 6 weeks of previous influenza vaccination.', 'A');
INSERT INTO event (label, event_type_code) values ('History of herpes zoster based on diagnosis', 'A');
INSERT INTO event (label, event_type_code) values ('History of thrombocytopenia', 'A');
INSERT INTO event (label, event_type_code) values ('History of thrombocytopenic purpura', 'A');
INSERT INTO event (label, event_type_code) values ('History of varicella based on diagnosis', 'A');
INSERT INTO event (label, event_type_code) values ('HIV infection', 'A');
INSERT INTO event (label, event_type_code) values ('HIV Infection CD4+ T lymphocyte count < 200 cells/microliter', 'A');
INSERT INTO event (label, event_type_code) values ('HIV Infection CD4+ T lymphocyte count >= 200 cells/microliter', 'A');
INSERT INTO event (label, event_type_code) values ('Household contacts of hepatitis B surface antigen-positive persons', 'A');
INSERT INTO event (label, event_type_code) values ('Immune suppression', 'A');
INSERT INTO event (label, event_type_code) values ('Immunocompromised persons (including those with HIV infection)', 'A');
INSERT INTO event (label, event_type_code) values ('Immunocompromising conditions', 'A');
INSERT INTO event (label, event_type_code) values ('Immunocompromising conditions (excluding HIV)', 'A');
INSERT INTO event (label, event_type_code) values ('Immunodeficiency from hematologic tumors', 'A');
INSERT INTO event (label, event_type_code) values ('Immunodeficiency from long-term immunosuppressive therapy', 'A');
INSERT INTO event (label, event_type_code) values ('Immunodeficiency from receipt of chemotherapy', 'A');
INSERT INTO event (label, event_type_code) values ('Immunodeficiency from solid tumors', 'A');
INSERT INTO event (label, event_type_code) values ('International travelers to countries with high or intermediate prevalence of chronic HBV infection', 'A');
INSERT INTO event (label, event_type_code) values ('Kidney disease', 'A');
INSERT INTO event (label, event_type_code) values ('Kidney failure', 'A');
INSERT INTO event (label, event_type_code) values ('Known severe immunodeficiency', 'A');
INSERT INTO event (label, event_type_code) values ('Leukemia', 'A');
INSERT INTO event (label, event_type_code) values ('Men who have sex with men (MSM)', 'A');
INSERT INTO event (label, event_type_code) values ('Microbiologists routinely exposed to isolates of Neisseria meningitidis', 'A');
INSERT INTO event (label, event_type_code) values ('Military recruits', 'A');
INSERT INTO event (label, event_type_code) values ('Moderate or severe acute illness with or without fever', 'A');
INSERT INTO event (label, event_type_code) values ('Nephrotic syndrome', 'A');
INSERT INTO event (label, event_type_code) values ('Other hemoglobinopathies', 'A');
INSERT INTO event (label, event_type_code) values ('Patients receiving hemodialysis', 'A');
INSERT INTO event (label, event_type_code) values ('Patients with human immunodeficiency virus (HIV) infection who are severely immunocompromised', 'A');
INSERT INTO event (label, event_type_code) values ('Persistent complement component deficiencies.', 'A');
INSERT INTO event (label, event_type_code) values ('Persons seeking evaluation for a sexually transmitted disease (STD)', 'A');
INSERT INTO event (label, event_type_code) values ('Persons seeking treatment for a sexually transmitted disease (STD)', 'A');
INSERT INTO event (label, event_type_code) values ('Persons traveling to countries that have high or intermediate endemicity of hepatitis A', 'A');
INSERT INTO event (label, event_type_code) values ('Persons who anticipate close personal contact (e.g., household or regular babysitting) with an international adoptee during the first 60 days after arrival in the United States from a country with high or intermediate endemicity.', 'A');
INSERT INTO event (label, event_type_code) values ('Persons who experience only hives with exposure to eggs', 'A');
INSERT INTO event (label, event_type_code) values ('Persons who live in countries in which meningococcal disease is hyperendemic or epidemic', 'A');
INSERT INTO event (label, event_type_code) values ('Persons who receive clotting factor concentrates', 'A');
INSERT INTO event (label, event_type_code) values ('Persons who travel to countries in which meningococcal disease is hyperendemic or epidemic', 'A');
INSERT INTO event (label, event_type_code) values ('Persons working in countries that have high or intermediate endemicity of hepatitis A', 'A');
INSERT INTO event (label, event_type_code) values ('Persons working with HAV in a research laboratory setting', 'A');
INSERT INTO event (label, event_type_code) values ('Persons working with HAV-infected primates', 'A');
INSERT INTO event (label, event_type_code) values ('Pregnancy', 'A');
INSERT INTO event (label, event_type_code) values ('Public-safety workers who are potentially exposed to blood', 'A');
INSERT INTO event (label, event_type_code) values ('Public-safety workers who are potentially exposed to infectious body fluids', 'A');
INSERT INTO event (label, event_type_code) values ('Receipt of hemodialysis', 'A');
INSERT INTO event (label, event_type_code) values ('Receipt of specific antivirals (i.e., acyclovir, famciclovir, or valacyclovir)', 'A');
INSERT INTO event (label, event_type_code) values ('Receipt of specific antivirals (i.e., amantadine, rimantadine, zanamivir, or oseltamivir)', 'A');
INSERT INTO event (label, event_type_code) values ('Residents of long-term care facilities', 'A');
INSERT INTO event (label, event_type_code) values ('Residents of nursing homes', 'A');
INSERT INTO event (label, event_type_code) values ('Severe allergic reaction (e.g., anaphylaxis) after a previous dose or to a vaccine component', 'A');
INSERT INTO event (label, event_type_code) values ('Severe allergic reaction (e.g., anaphylaxis) after a previous dose or to a vaccine component, including to any vaccine containing diphtheria toxoid', 'A');
INSERT INTO event (label, event_type_code) values ('Severe allergic reaction (e.g., anaphylaxis) after previous dose of any influenza vaccine or to a vaccine component', 'A');
INSERT INTO event (label, event_type_code) values ('Severe allergic reaction (e.g., anaphylaxis) after previous dose of any influenza vaccine or to a vaccine component, including egg protein.', 'A');
INSERT INTO event (label, event_type_code) values ('Severe allergic reaction (e.g., anaphylaxis) to a vaccine component', 'A');
INSERT INTO event (label, event_type_code) values ('Sex partners of hepatitis B surface antigen-positive persons', 'A');
INSERT INTO event (label, event_type_code) values ('Sexually active persons who are not in a long-term, mutually monogamous relationship (e.g., persons with more than one sex partner during the previous 6 months)', 'A');
INSERT INTO event (label, event_type_code) values ('Sickle cell disease', 'A');
INSERT INTO event (label, event_type_code) values ('Splenectomy', 'A');
INSERT INTO event (label, event_type_code) values ('Splenic dysfunction', 'A');
INSERT INTO event (label, event_type_code) values ('Staff members of institutions for persons with developmental disabilities', 'A');
INSERT INTO event (label, event_type_code) values ('Symptomatic HIV infection', 'A');
INSERT INTO event (label, event_type_code) values ('Users of injection and non-injection illicit drugs', 'A');
INSERT INTO event (label, event_type_code) values ('Verification of herpes zoster disease by a health-care provider', 'A');
INSERT INTO event (label, event_type_code) values ('Verification of varicella disease by a health-care provider', 'A');

INSERT INTO event (label, event_type_code) values ('Healthy Patient', 'N');
INSERT INTO event (label, event_type_code) values ('Infants have increased risk of contracting pertussis', 'N');
INSERT INTO event (label, event_type_code) values ('Infants have increased risk of contracting pertussis from unvaccinated Adults', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of Tetanus', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk for hepatitis A', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk for hepatitis B', 'N');
INSERT INTO event (label, event_type_code) values ('Child/adolescent is at increased risk for hib', 'N');
INSERT INTO event (label, event_type_code) values ('Adult is at increased risk for hib', 'N');
INSERT INTO event (label, event_type_code) values ('Patient must restart hib vaccination', 'N');
INSERT INTO event (label, event_type_code) values ('Patient infected with invasive hib disease', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of HPV', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at an increased risk for Japanese Encephalitis', 'N');
INSERT INTO event (label, event_type_code) values ('Patient (2m through 18m) is at increased risk for invasive meningococcal disease', 'N');
INSERT INTO event (label, event_type_code) value('Patient (9m through 23m) is at increased risk for invasive meningococcal disease', 'N');
INSERT INTO event (label, event_type_code) values ('Patient (2y through 55y)  is at increased risk for invasive meningococcal disease due to medical condition', 'N');
INSERT INTO event (label, event_type_code) values ('Patient (2y through 55y)  is at increased risk for invasive meningococcal disease due to travel, living, or occupational condition ', 'N');
INSERT INTO event (label, event_type_code) values ('Patient (56y+) is at increased risk for invasive meningococcal disease', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk for MMR', 'N');
INSERT INTO event (label, event_type_code) values ('Child/adolescent is at increased risk for pneumococcal', 'N');
INSERT INTO event (label, event_type_code) values ('Adult is at increased risk for pneumococcal', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of polio', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk for rabies', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at high risk or lives with/cares for a person at high risk for medical complications attributable to severe influenza', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of S.typhi infection', 'N');
INSERT INTO event (label, event_type_code) values ('Patient may be at increased risk for exposure or transmission to varicella, without evidence of immunity', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of yellow fever infection due to travel', 'N');
INSERT INTO event (label, event_type_code) values ('Patient is at increased risk of yellow fever infection due to occupation', 'N');

-- INSERT INTO recommend (recommend_text, recommend_type_code, recommend_range_code) VALUES ('Do not administer any LAIV product', 'C', 'T');
-- INSERT INTO consideration (consideration_text, consideration_type_code) VALUES ('Patient with history of a life-threatening allergic reaction after a dose of flu vaccine, or a severe allergy to any part of this vaccine, including (for example) an allergy to gelatin, antibiotics, or eggs, may be advized to not get vaccinated', 'G');
-- INSERT INTO consideration (consideration_text, consideration_type_code) VALUES ('Patient with a history of GBS should not get this vaccine. ', 'G');
-- INSERT INTO rationale (rationale_text) VALUES ('Live virus vaccines, such as LAIV, should only be administered 28 or more days after a previously given live virus vaccine.');
-- INSERT INTO resource (resource_text, resource_link) VALUES ('Live Attenuated Vaccines ', 'http://vaccine-safety-training.org/live-attenuated-vaccines.html');


