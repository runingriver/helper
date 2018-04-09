DROP TABLE IF EXISTS date_string_test;
CREATE TABLE IF NOT EXISTS date_string_test (
  id                           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  sql_date_to_java_string      DATETIME        NOT NULL DEFAULT '1970-01-01 00:00:00',
  sql_date_to_java_date        DATETIME        NOT NULL DEFAULT '1970-01-01 00:00:00',
  sql_timestamp_to_java_string TIMESTAMP       NOT NULL DEFAULT '1970-01-01 12:00:00',
  sql_timestamp_to_java_date   TIMESTAMP       NOT NULL DEFAULT '1970-01-01 12:00:00',
  java_string_to_sql_date      DATETIME        NOT NULL DEFAULT '1970-01-01 00:00:00',
  java_string_to_sql_timestamp TIMESTAMP       NOT NULL DEFAULT '1970-01-01 12:00:00',
  sql_string_to_java_date      VARCHAR(50)     NOT NULL DEFAULT '',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;