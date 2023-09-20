# Changes for lovely-db-testing

## unreleased

### Fix

- properly await postgres server container startup

### Feature

- update gradle to 8.3
- update kotlin to 1.9.10
- update lovely gradle plugin to 1.12.0
- update testcontainers to 1.19.0

## 2023-08-14 / 0.1.0

### Feature

- allow to specify a fixed port for the server container
- allow to reuse server container in test setup
- upgraded test containers to 1.18.3

### Develop

- upgraded kotlin and gradle

## 2022-02-13 / 0.0.5

- allow to filter sql test files to run via env var 'SQLTEST_FILE_FILTER'
- test setup uses docker-postgres 14.1.0 as default

## 2021-12-05 / 0.0.4

- seperated testsetup from junit specific stuff to make usable with other frameworks like kotest

## 2021-10-23 / 0.0.3

- fix recognition of regex pattern

## 2021-10-22 / 0.0.2

- allow to configure server container in setup
- allow to null the test and dev directory in pg setup

## 2021-10-19 / 0.0.1

- initial junit extension implemented
