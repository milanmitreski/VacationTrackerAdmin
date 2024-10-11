# VacationTrackerAdmin

Spring Boot based microservice designed for importing vacation data.

Usage:

After cloning the repo, run the following command (inside your working directory, which should be named `VacationTrackerAdmin`)

``` 
./gradlew build 
```

This should create an executable `.jar` in `build/libs/`.

After you've finished building the executable `.jar`, you should follow the building instructions for [VacationTrackerEmployee](https://github.com/rbt-mmitreski/VacationTrackerEmployee/) service. These two services should share the same parent directory.

Both service run on a PostgreSQL database. The creation script `test-schema.sql` can be found in this project. Note that you should install the software needed (which can be found [here](https://www.postgresql.org/)). Before starting the services, run the creation script with the `psql` command. For more information, read the documentation [here](https://www.postgresql.org/docs/current/app-psql.html). 

To run the services, run the following command inside your `VacationTrackerAdmin` directory:

```
docker compose up
```

Note that you should install Docker on your system. Services will be available at `https://localhost:8081` (admin service) and `https://localhost:8082` (employee service). After starting the services, Swagger-based OpenAPI3 SpringDocs will be available [here(admin)](http://localhost:8081/swagger-ui.html) and [here(employee)](http://localhost:8082/swagger-ui.html).
