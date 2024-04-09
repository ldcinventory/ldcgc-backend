# LDC Inventory Backend

> A simple tool to manage volunteers working in LDC department, and manage tools and consumables, along with the registration of these to volunteers.

---
## Don't care about the details? Let's run this in less than 10 minutes

- Download docker from https://docs.docker.com/engine/install/, install it and run it.
- Go to https://github.com/settings/tokens/new, create **a classic token**. Give the only permission of _read:packages_ (_Download packages from GitHub Package Registry_). 
- Copy the generated token. Go to the terminal. Run this command: `docker login ghcr.io -u your_github_user`, and paste the token from GitHub.
- Put the CSVs into `src/main/resources` folder (ask L for them).
- Run this:
  - If you're backend: `./run.sh create`
  - If you're frontend: `./run.sh create-api`
  - If you're QA: `./run.sh create-ui` (you'll need both frontend and backend projects in local).

## Run in Cloud

> (not yet implemented)

### Login

To perform a login in this API, just go with default credentials for testing purposes (check others **in EULA section** to temporarily avoid EULA):

> **ADMIN**. email: `admin@admin` Password: `admin`
>
> **MANAGER**. email: `manager@manager` Password: `manager`
>
> **USER**. email: `user@user` Password: `user` 
>
> **USER+VOLUNTEER**. email: `volunteer@volunteer` Password: `volunteer`. This is an account with user + volunteer associated (_needs the CSV files to test on local_).

Call the api to endpoint `/api/account/login` with the payload:

```
{
  "email": "user@user",
  "password": "user"
}
```

### Environment Variables

Is necessary indicate some values as environment variables:

**SPRING BOOT**

+ `ENVIRONMENT_PROFILE` (mandatory for make it run in cloud and local environment (not docker)): values `dev` or `pro`. Used to select Spring profile to use on startup (default `dev`).
+ `JWT_EXPIRATION`: set value in seconds the time the JWT is valid.
+ `TOOLS_REGISTRATION_TEST_DATA`: a boolean to load test data for tools registration.
+ `CONSUMABLES_REGISTRATION_TEST_DATA`: a boolean to load test data for consumables registration.

**DB**

+ `DB_HOST`: host of database.
+ `DB_NAME`: name of database.
+ `DB_USER`: user to login on database server.
+ `DB_PASS`: password for login.
+ `DB_START_MODE`: hibernate ddl-auto options: `none` (for production), `create-drop`, `create`, `drop`, `update` (default for dev), `validate`.
+ `LOAD_INITIAL_DATA`: only used on development mode. It is used to indicate if the bootstrap process must load or not data from csv local file.
+ `CSV_FILE`: file name which contains all the data to insert. It should be placed into `resources` folder.

**MAIL**

+ `MAIL_HOST`: host of the mail container (default `localhost`).
+ `MAIL_SMTP_PORT`: port of the mail container (default `1025`).
+ `MAIL_USER`: username of the mail container (default `admin`).
+ `MAIL_PASS`: password of the mail container (default `mypass`).
+ `MAIL_TLS_ENABLED`: TLS for mail is enabled (default `false`).
+ `MAIL_TLS_REQUIRED`: TLS for mail is required (default `false`).

**LOGGING** (values could be `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`, `OFF`)

+ `LOGGING_ROOT`: level of logging for the root package, which contains all packages in app (default `ERROR`).
+ `LOGGING_SPRING_WEB`: level of logging for the spring boot web framework (default `ERROR`).
+ `LOGGING_SPRING_BOOT`: level of logging for the spring boot auto-configuration information (default `ERROR`).
+ `LOGGING_API`: level of logging for the API package (default `ERROR`).
+ `LOGGING_HIBERNATE`: level of logging for the hibernate queries and functionality (default `ERROR`).
+ `LOGGING_TESTS`: level of logging for the tests (default `ERROR`).

---

## Run using Docker

### Mandatory configuration (pre-initialization)

Please make sure you have access to _ghcr_ base images, which are stored into GitHub Repository instead of Docker Hub. To do so, just create a personal token in the url: https://github.com/settings/tokens/new, to create a classic token from your account. Give the only permission of _read:packages_ (_Download packages from GitHub Package Registry_).

Then, when created, copy the generated number and go to the terminal. Run this command: `docker login ghcr.io -u your_github_user`.

Paste the token previously copied and that's all. Now docker will be able to download images from GitHub Repository.

### Initialization

For make-it-easier-for-you purposes, you can run:

`./run.sh` in MacOS/Linux based system, which will delete previous `docker` api compilations **only** with this project's compose file, and will start from zero cleaning containers, then images, then volumes, and restarting all dependencies again :) (please notice only API is completely erased, not other official images, like DB or SMTP services).

You should provide 1 option (it won't run with just `./run.sh`).

#### RUN just API dependencies (i.e. DB + SMTP)

This option is mainly use by the Backend side of the project. The options used for run in this mode are:

- `create`: create the container dependencies
- `restart`: just restart the containers

#### RUN API (i.e. API + DB + SMTP)

This option is mainly use by the Frontend side of the project. The options used for run in this mode are:

- `create-api`: create the API with CSVs (you need those inside `src/main/resources`)
- `create-test-data-api`: create the API with CSVs and test data for consumible and tool registers
- `restart-api`: just restart the containers

#### RUN UI (i.e. UI + API + DB + SMTP)

This option is mainly use by the QA side of the project. The options used for run in this mode are:

- `create-ui`: create the UI + API with CSVs (you need those inside `src/main/resources`)
- `create-test-data-ui`: create the UI + API with CSVs and test data for consumible and tool registers
- `restart-ui`: just restart all the containers

#### PURGE EVERYTHING

- `purge`: clean all the data in Docker (images included!)

### How to access to everything

When everything's is OK with DB, you can use this command to test backend healthiness:

- Check API is alive running `curl -v http://localhost:8080/api/alive` or directly putting that url in a web browser (it **should** respond with a 200 OK).
- Work with API @ http://localhost:8080/api, better from postman 😉
- Swagger @ https://localhost:8080/api/swagger-ui/index.html"

### STOP
To stop and clean, you can shutdown the multi-container with:

`./run.sh purge`

---
## DB
> In case you want to use your own PostgresSQL instance, you can run the `docker-compose.yml` file with `docker-compose up` command. Also, you can play with the parameters inside that file to configure PostgresSQL instance.

Make sure you have a PostgresSQL DB up and running with the following default credentials (if you didn't change anything) for testing environment from any IDE (when running `docker-compose up` command):
```
db: mydb
username: myuser
password: mypassword
url: localhost:5434
```

---

## SMTP
> Just in case you want to use your own MailDev / SMTP instance. It needs to change the `docker-compose.yml` file.

You can expect to test the smtp test server with web UI access directly to `localhost:1026`, to check if mails come directly from backend to this **fake** smtp service.

---

## EULA

The end user license agreement is the contract between the user and the application, and regulates the terms and conditions for every person in the application to use it. When a user tries to login and that user didn't accept the terms and conditions to use the app, or when the EULA has changed and the user is already logged (i.e. they have a valid token and EULA changes while using the app), the app will return a `403 FORBIDDEN` in order to prevent a user can change anything without EULA accepted.

A standard user may accept a standard EULA, a manager or admin may accept a standard and a manager EULA.

**Temp**: to skip eula while frontend is aligning this functionality, some users are provided with EULA accepted:

> **ADMIN USER**  email: `noeula@admin`, pass: `admin `
> 
> **MANAGER USER**. email: `noeula@manager`, pass: `manager`
> 
> **STANDARD USER**. email: `noeula@user`, pass: `user`
> 
> **ADMIN USER+VOLUNTEER**. email: `noeula@adminv`, pass: `admin`
>
> **MANAGER USER+VOLUNTEER**. email: `noeula@managerv`, pass: `manager`
>
> **STANDARD USER+VOLUNTEER**. email: `noeula@userv`, pass: `user`

---

## Login Response

When the login endpoint was invoked, it returns a JSON response with a token in 2 special headers:

```
{
  ...
  "x-header-payload-token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzM2MyMjM5Yi1hZWNhLTQ5NTYtODI1MC0yMjI2Mjg0MDI3MDEiLCJhdWQiOiJhZG1pbiIsInVzZXJJbmZvIjp7InJvbGUiOiJST0xFX0FETUlOIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSJ9LCJpc3MiOiJpUHJlYWNoIiwiZXhwIjoxNjgxMzEzMDA4LCJpYXQiOjE2ODEyMjY2MDgsImp0aSI6IjBjZDhkZGRkLTQ5YmQtNGU4NC05MzhmLTM1ZWNkNTQ5YWQzZiJ9",
  "x-signature-token": "Ig1A0YTnLLPvTDyFK1fvQdwYY18ac2qaVwEUfqTVKZGvTOPbqj-s76TBDL14ZT03vEX0HjTS1b82H-0ZY4suTg"
}
```

Both headers are required to call the most part of the endpoints of the app.


**In the future**, we will provide **refresh token** to avoid accounts be disconnected after 24 hours (default time for a token to expire).

---

## Swagger

This project builds a Swagger portal directly from code, in which you can explore the available endpoints. The url to access this resource is in: http://localhost:8080/api/swagger-ui/index.html

---

Have fun ;)
