DROP TABLE IF EXISTS LOG_EVENT;

CREATE TABLE LOG_EVENT (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  message VARCHAR(250) NOT NULL,
  occurredOn timestamp not null ,
  level      varchar(40) not null
);