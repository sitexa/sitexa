CREATE TABLE Medias
(
  id         INT AUTO_INCREMENT PRIMARY KEY,
  ref_id     INT          NULL,
  file_name  VARCHAR(50)  NOT NULL,
  file_type  VARCHAR(20)  NOT NULL,
  title      VARCHAR(100) NULL,
  sort_order INT          NULL
)
  ENGINE = InnoDB;

CREATE INDEX Medias_sort_order
  ON Medias (sort_order);

CREATE TABLE Posts
(
  id          INT          NOT NULL   PRIMARY KEY,
  post_number VARCHAR(6)   NOT NULL,
  province    VARCHAR(100) NULL,
  city        VARCHAR(100) NULL,
  district    VARCHAR(100) NULL,
  address     VARCHAR(100) NULL,
  jd          VARCHAR(100) NULL
)
  ENGINE = InnoDB;

CREATE TABLE Sites
(
  id        INT            NOT NULL   PRIMARY KEY,
  code      INT            NOT NULL,
  parent_id INT            NOT NULL,
  name      VARCHAR(200)   NOT NULL,
  level     INT            NOT NULL,
  lat       DECIMAL(10, 6) NULL,
  lng       DECIMAL(10, 6) NULL,
  CONSTRAINT code_UNIQUE
  UNIQUE (code)
)
  ENGINE = InnoDB;

CREATE TABLE Sweets
(
  id              INT AUTO_INCREMENT PRIMARY KEY,
  user_id         VARCHAR(20) NOT NULL,
  date            DATETIME(6) NOT NULL,
  reply_to        INT         NULL,
  direct_reply_to INT         NULL,
  text            TEXT        NOT NULL
)
  ENGINE = InnoDB;

CREATE INDEX Sweets_user_id
  ON Sweets (user_id);

CREATE INDEX Sweets_reply_to
  ON Sweets (reply_to);

CREATE INDEX Sweets_direct_reply_to
  ON Sweets (direct_reply_to);

CREATE TABLE Users
(
  id            VARCHAR(20)  NOT NULL   PRIMARY KEY,
  mobile        VARCHAR(15)  NOT NULL,
  email         VARCHAR(128) NOT NULL,
  display_name  TEXT         NOT NULL,
  password_hash VARCHAR(64)  NOT NULL,
  CONSTRAINT Users_mobile_unique
  UNIQUE (mobile),
  CONSTRAINT Users_email_unique
  UNIQUE (email)
)
  ENGINE = InnoDB;

create table Test(
  id VARCHAR(2) NOT NULL PRIMARY KEY ,
  name VARCHAR(10) NOT NULL
) ENGINE = InnoDB, DEFAULT CHARSET = utf8;
