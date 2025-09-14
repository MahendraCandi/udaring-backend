## How to run using docker

1. Inside the project root, run:
```shell
sudo docker compose up --build
```

if success, the compose will running PostgresSQL database and backend application.

2. Access the backend application at:
```
http://localhost:8080/swagger-ui/index.html
```

3. Access the database from any database editor for PostgresSQL:
```
DB_URL=jdbc:postgresql://localhost:5432/udaring_database
DB_USER=user
DB_PASSWORD=password
```

4. For Mac user who installed the docker engine manually (without docker desktop) by using `brew` and using `colima`.\
For point 1, use this command instead:
```shell
sudo docker-compose up --build
```
