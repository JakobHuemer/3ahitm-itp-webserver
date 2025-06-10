DROP SCHEMA IF EXISTS app CASCADE;
CREATE SCHEMA app;

CREATE TABLE app.greeting (
  id INTEGER PRIMARY KEY,
  greeting varchar(255)
);

insert into app.greeting (id, greeting) values
(1, 'Hallo Welt'),
(2, 'Hallo Chris'),
(3, 'Hallo Hanan');