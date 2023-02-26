# <center>LatteViz API</center>

### <center> ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)</center>


## About project

This is a project developed for the subject of Systems Development 1.

This project bois down to the back-end application.


## Environment Set Up

* __SDK__: Java 17
* __Framework__: Spring Boot
* __Database__: PostgresSQL 14
* __Data Base Manager__: DBeaver or pgAdmin

## Environment Configuration

1. ### User creation in the database
```sql
--Create de USER that we use to link with our application

CREATE USER latte_admin  WITH PASSWORD 'latte_admin' SUPERUSER CREATEDB;

-- To verify if the user was created
    
SELECT * from pg_catalog.pg_user;
```

2. ### Database creation

```sql
CREATE DATABASE latte-db;
```