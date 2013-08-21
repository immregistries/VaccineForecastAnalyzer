CREATE TABLE login_attempt
(
  login_attempt_id  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, 
  login_date        DATETIME NOT NULL,
  name              VARCHAR(30) NOT NULL,
  password          VARCHAR(30) NOT NULL,
  user_id           INTEGER
);