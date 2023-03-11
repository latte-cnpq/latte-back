# <center>LatteViz API</center>

### <center> ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)</center>


## Sobre o projeto

Esse é um projeto desenvolvido para a matéria de Desenvolvimento de Sistemas 1.

Este projeto se resume à aplicação de back-end.

## Requisitos do ambiente

* __SDK__: Java 17
* __Framework__: Spring Boot
* __Database__: PostgresSQL 14
* __Data Base Manager__: DBeaver or pgAdmin

## Environment requirements

* __SDK__: Java 17
* __Framework__: Spring Boot
* __Database__: PostgresSQL 14
* __Data Base Manager__: DBeaver or pgAdmin


## Configuração do ambiente

1. ### Criação do usuário no banco de dados
```sql
--Crie o usuário que nós usamos para conectar a aplicação

CREATE USER latte_admin  WITH PASSWORD 'latte_admin' SUPERUSER CREATEDB;

-- Para verificar se o usuário foi criado
    
SELECT * from pg_catalog.pg_user;
```

2. ### Criação do banco de dados

```sql
CREATE DATABASE latte-db;
```

3. ### Vairáveis de ambiente

| Nome       |Valor|
|------------|-----|
| PGDATABASE |latte-db|
| PGHOST     |localhost|
| PGPASSWORD |latte_admin|
| PGPORT     |5432|
| PGUSER     |latte_admin|


---



## About project

This is a project developed for the subject of Systems Development 1.

This project bois down to the back-end application.


## Environment requirements

* __SDK__: Java 17
* __Framework__: Spring Boot
* __Database__: PostgresSQL 14
* __Data Base Manager__: DBeaver or pgAdmin


## Environment Configuration

1. ### User creation in the database
```sql
--Create the USER that we use to link with our application

CREATE USER latte_admin  WITH PASSWORD 'latte_admin' SUPERUSER CREATEDB;

-- To verify if the user was created
    
SELECT * from pg_catalog.pg_user;
```

2. ### Database creation

```sql
CREATE DATABASE latte-db;
```

3. ### Environment variable

| Nome       |Valor|
|------------|-----|
| PGDATABASE |latte-db|
| PGHOST     |localhost|
| PGPASSWORD |latte_admin|
| PGPORT     |5432|
| PGUSER     |latte_admin|