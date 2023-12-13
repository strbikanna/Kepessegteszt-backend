# Képességteszt portál - HU

## Projekt felépítése:

- Autorization Server: backend/auth_server
- Resource Server: backend/resource_server
- Frontend: frontend/cognitive-app-frontend

## Lokális futtatáshoz szükséges eszközök:

- Gradle 8.2.1
- JDK 17+
- Python 3.9+
  - tensorflow
  - statsmodels
  - scikit-learn
  - numpy
  - joblib
  - jep
- Jep on classspath
- MySQL db
- npm
- ng CLI

Adatbázis elérést általában biztosítani kell a megfelelő felhasználókkal, de maga a séma automatikusan fel lesz húzva az alkalmazás indításakor.

## Resource Server

REST API a felhasználói adatok, kognitív profil és játékok adatainak elérésére.

### Profilok

Az alkalmazás több futtatási profilt használ, ezek közül válasszuk a megfelelőt. A jar file futtatása profillal -Dspring.profiles.active:{profil} kapcsolóval lehetséges.

1. Default: lokális MySQL szerver, cognitive_app adatbázis és localhost:8090 port, szükséges a statikus file mappák elérési útvonalának megadása az application.yaml file-ban. Ugyanitt található a default user és password, amivel az adatbázis elérhető. Igény szerint módosítandó.
2. Test: tesztekhez használt, in-memory adatbázissal, securtiy bypass beállítással. Az adatbázis triggerek és tárolt eljárások nem futnak a H2 adatbázissal, alapértelmezés szerint a Python környezet sem elérhető. Ez módosítható az application-test.yaml file-ban.
3. Container: konténerben futtatható beállítás, globálisan elérhető adatbáziscímet kell biztosítani (localhost nem megfelelő, hiszen az a konténnerben nem létezik). A beállítások az application-container.yaml fájl-ban módosíthatók. Környezeti változókkal beállítható a használt port, az autentikációs szerver címe, a konténerben értelmezett statikus file elérési útvonal. (Ez a Dockerfile-ban be van állítva.)

### Kipróbálás

Alapvetően csak az auth-serverrel együtt működik, mivel a legtöbb endpoint védett. Autentikáció nélkül elérhető a swagger dokumentáció: /swagger-ui/index.html, illetve a tárolt képek pl. /game/images/cosmic.jpg
Python környezet és JEP library jiánya nem okoz fordítás idejű problémát, de bizonyos funkciók exception-t dobnak.

### REST dokumentáció

Elérhető a /api-docs ill. a /swagger-ui/index.html oldalon.

## Authorization Server

OAuth2 autentikációs szerver külön adatbázissal, role-based JWT tokenekkel.

### Profilok

Az alkalmazás több futtatási profilt használ, ezek közül válasszuk a megfelelőt. A jar file futtatása profillal -Dspring.profiles.active:{profil} kapcsolóval lehetséges.

1. Default: lokális MySQL szerver, app_users adatbázis és localhost:9000 port.
2. Test: tesztekhez használt, in-memory adatbázis.
3. Dev: szintén in-memory adatbázis. Ez használható, ha nincs elérhető MySQL szerver.
4. Container: konténerben futtatható beállítás, globálisan elérhető adatbáziscímet kell biztosítani (localhost nem megfelelő, hiszen az a konténnerben nem létezik). Környezeti változókkal beállítható a használt port, az autentikációs szerver címe, ami a tokenek kiállítása és az email küldés miatt fontos. (Ez a Dockerfile-ban be van állítva.)

### Kipróbálás

Postman segítségével kipróbálható, a kliens be van regisztrálva. Id: postman-client-007, secret: 123. Ezen kívül meg kell adni a scope-ot: openid. Opcionális paraméterek küldhetők pl. act_as {username}.
A mr regisztrált felhasználók belépési adatai megtalálhatók a backend/auth_server/src/main/resources/db/migration/V1_2\_\_init_data.sql fájlban.

## Frontend

Kliens alkalmazás, mely a két backend-hez kapcsolódik. Néhány Phaser-játék is integrálva van, melyek külön megtalálhatók a cognitive-testing-games repository-ban.
Futtatás ng server paranccsal alapértelmezs szerint development környezetben történik, localhost:4200 porton.
A környezeti változók az enviroments/environment.development.ts-ben módosíthatók. Ezek közé tartozik a két szerver URL címe, ill. a frontend címe.
Megjegyzés: A frontend címének megváltoztatása esetén az auth server registered-client tábláján is módosítni kell, különben nem lehet bejelentkezni.

---

# Cognitive Test - ENG

## Project Structure:

- Authorization Server: backend/auth_server
- Resource Server: backend/resource_server
- Frontend: frontend/cognitive-app-frontend

## Tools Required for Local Development:

- Gradle 8.2.1
- JDK 17+
- Python 3.9+
  - tensorflow
  - statsmodels
  - scikit-learn
  - numpy
  - joblib
  - jep
- Jep on classpath
- MySQL db
- npm
- ng CLI

Database access usually needs to be provided with appropriate users, but the schema will be automatically created when the application is launched.

## Resource Server

REST API for accessing user data, cognitive profile, and game data.

### Profiles

The application uses multiple runtime profiles; choose the appropriate one when running the JAR file using the -Dspring.profiles.active:{profile} switch.

1. Default: local MySQL server, cognitive_app database, localhost:8090 port; specify the path to the static file folders in the application.yaml file. The default username and password for accessing the database are also provided there and can be modified as needed.
2. Test: used for tests with an in-memory database and security bypass settings. Triggers and stored procedures do not run with the H2 database by default. The Python environment is also not available by default, but this can be modified in the application-test.yaml file.
3. Container: configuration for running in a container; provide a globally accessible database address (localhost is not suitable as it doesn't exist in the container). Settings can be modified in the application-container.yaml file. The port used, authentication server address, and the container's interpreted static file path can be set using environment variables (configured in the Dockerfile).

### Testing

It generally works only when combined with the auth-server since most endpoints are protected. Swagger documentation is accessible without authentication: /swagger-ui/index.html, as well as stored images, e.g., /game/images/cosmic.jpg. The absence of the Python environment and the JEP library doesn't cause compilation issues, but certain functions may throw exceptions.

### REST Documentation

Available at /api-docs and /swagger-ui/index.html.

## Authorization Server

OAuth2 authentication server with a separate database, role-based JWT tokens.

### Profiles

The application uses multiple runtime profiles; choose the appropriate one when running the JAR file using the -Dspring.profiles.active:{profile} switch.

1. Default: local MySQL server, app_users database, and localhost:9000 port.
2. Test: used for tests with an in-memory database.
3. Dev: also an in-memory database, can be used if no MySQL server is available.
4. Container: configuration for running in a container; provide a globally accessible database address (localhost is not suitable as it doesn't exist in the container). Settings can be modified using environment variables for the port used, authentication server address (important for token issuance and email sending). (Configured in the Dockerfile).

### Testing

Can be tested using Postman; the client is registered with ID: postman-client-007, secret: 123. Additionally, the scope must be provided: openid. Optional parameters can be sent, e.g., act_as {username}. The login details for newly registered users can be found in the backend/auth_server/src/main/resources/db/migration/V1_2\_\_init_data.sql file.

## Frontend

Client application that connects to the two backends. Some Phaser games are integrated, which can be found separately in the cognitive-testing-games repository. By default, it runs with the ng server command in development mode on localhost:4200.
Environment variables can be modified in environments/environment.development.ts. These include the URLs for the two servers and the frontend. Note: Changing the frontend address requires modifying the registered-client table in the auth server; otherwise, login will not be possible.
