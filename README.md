# scrum-service

## Requirements

* Java 11 (JDK + JRE) [Download](https://jdk.java.net/java-se-ri/11)
* Maven v3.6+ [Download](https://maven.apache.org/download.cgi)
* Running instance of postgres database


## Development

To build project you need to run in root of a project `mvn clean package` to build all the 
modules.

To build docker image, run previous maven command first and then `docker build -t scrum-service .` in root of a project. 

## Project structure

* `persistence`: module containing DB related code - schemas, migrations, entities
* `services`: module containing services (Java CDI beans) with application logic.
* `v1`:
  * `api`: module containing API definitions (endpoints), filters, mappers, etc.
  * `lib`: module containing data types that are exposed via APIs (public information).

### Configuration

Config file is in `api` module on path `v1/api/src/main/resources/config.yml`. All values can be 
overriden using environment values ie. key `kumuluzee.server.base-url` can be 
overriden by setting `KUMULUZEE_SERVER_BASEURL` environment variable.

## Database

To quickly setup database, you can use docker-compose in the root of a project with the following command:

```
docker-compose up -d scrum-postgres
```

This command runs only specified service from docker compose file.