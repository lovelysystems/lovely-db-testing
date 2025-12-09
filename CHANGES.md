# Changes for lovely-db-testing

## unreleased 

### Feature

- upgrade `testcontainers` to 2.0.2 to ensure compatibility with Docker 29+, since older versions use an unsupported Docker API.
- replaced deprecated method in PGClientContainer

### Breaking

- JUnit 4 support removed: @Rule @ClassRule and @Testcontainers/@Container annotations are no longer supported.
- Minimum Java bumped to 17

## 2024-02-08 / 0.3.0

### Fix

- close networks created by testcontainers inside `PGTestSetup.stop` to prevent having too many dangling networks around

### Feature

- update Gradle to 8.5
- update Kotlin to 1.9.22
- update testcontainers to 1.19.4

## 2023-09-21 / 0.2.0

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
