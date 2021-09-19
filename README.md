![Squash logo](src/main/resources/static/squash_logo.svg "Squash logo")

# Squash Java Spring Boot App (Backend)

This project is a backend part of an application to manage a squash league.

It is built with:

* Java 17
* Spring Boot 2
* Hibernate 5
* PostgreSQL 12


## Working demo

Application is up and running, and it is accessible at 
[www.squash-app.win](https://squash-app.win "https://squash-app.win").


## Launching locally

In order to launch it locally:

* pull the repository
* run `mvn clean install`
* create local PostgreSQL database
* apply below CLI arguments (replacing all `__REPLACE__` values according to your configuration) as most of them are needed to properly launch and use the app
```
--server.port=8082
--spring.jpa.database="POSTGRESQL"
--spring.jpa.properties.hibernate.dialect="org.hibernate.dialect.PostgreSQLDialect"
--spring.jpa.properties.hibernate.generate_statistics=false
--spring.jpa.properties.hibernate.format_sql=false
--spring.jpa.show-sql=false
--spring.datasource.platform="postgres"
--spring.datasource.driver-class-name="org.postgresql.Driver"
--spring.datasource.url="jdbc:postgresql://localhost:5432/__REPLACE__"
--spring.datasource.username=__REPLACE__
--spring.datasource.password=__REPLACE__
--jwt.secret=__REPLACE__
--email.sender_email_adress=__REPLACE__
--email.sender_name="Squash app"
--email.password=__REPLACE__
--email.smtp_host=__REPLACE__
--email.smtp_port=587
```
* run `Application` class from the IDE


## Frontend

In order to use the application, it must be run together with the frontend part, 
which is available at 
[pChocz/squash-angular](https://github.com/pChocz/squash-angular "https://github.com/pChocz/squash-angular").

Follow instructions there as well if you want to run whole application locally.


## License

>You can check out the full license [here](https://github.com/pChocz/squash-rest-api/blob/master/LICENSE)

This project is licensed under the terms of the **MIT** license.
