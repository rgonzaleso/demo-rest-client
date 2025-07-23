# Demo for RestClient without RestTemplate

This a project for demo purpose.

- It was developed using `springboot 3.5.3`, `postgres 15`, `maven 3.8` and `java 21`
- Include `spring web`, `spring jpa`, `spring security` and `spring validation`.
- Also, libraries like `flyway`, `test containers` and `swagger` 
- Finally, the application was dockerized and included unit and integration tests.

## Features

- The application every hour automatically make a request to Alogia API to retrieve data and store into a `postgres` database.
- Also, expose a service to show the data using filters like: author, tags, title and month.
- Also, It is possible to hide items that you don't want to view.
- For security, you need to login to use the services exposed

## Start the Application

- The first time you need to package the project using maven (execute located at the root of the project directory)
```bash
     ./mvnw clean package -DskipTests   
 ```
- Then using docker to start the application and the database (the app init at port 8080)
```bash
    docker compose up --build  
```
- **For complete restore:** you need to delete the volume of the database using the following command then do the previous steps.
```bash
    docker compose down -v     
```

## Using the Application

- You can use postman client follow next instructions.
- First, you need to login. This is the unique user, and it is inserted into the database using flyway in the file: `src/main/resources/db/migration/V2__initial_configuration.sql`
```
    POST http://localhost:8080/auth/login
    Content-Type: application/json
    
    {
      "username": "rgonzaleso",
      "password": "rgonzaleso"
    }
```
- Next, **as an assumption** you can retrieve the initial data using data following service and using the token returned by the login service. 
```
    GET http://localhost:8080/api/v1/hits/seed
    Authorization: Bearer token
```
- Next, you can find the data using the following service. This return paginated data by the following filters using query parameters: 
`author`, `month`, `title` and `tags`. This is an example: (`page` is used to retrieve paginated data)
```
    GET http://localhost:8080/api/v1/hits/find?page=0&author=achierius&motnh=july&title=credit&tags=comment&tags=author_achierius
    Authorization: Bearer token
```
- Finally, for hide items, you can use:
```
    POST localhost:8080/api/v1/hits/hide
    Authorization: Bearer token
    Content-Type: application/json
    
    {
      "ids": [1,2]
    }
```

## Final comments

You can find the swagger documentation in: 
```
    http://localhost:8080/api/docs
```