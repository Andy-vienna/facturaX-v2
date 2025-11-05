-- Datei: create_two_databases.sql
\set ON_ERROR_STOP on

DROP DATABASE IF EXISTS "fxDBdata";
DROP DATABASE IF EXISTS "fxDBwork";

CREATE DATABASE "fxDBdata"
    WITH
    OWNER = data
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE DATABASE "fxDBwork"
    WITH
    OWNER = data
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
	