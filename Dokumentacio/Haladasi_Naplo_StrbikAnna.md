# 09.18.
## Ezen a héten a következőket valósítottam meg:
Ezen a héten a specifikáció pontos leírásán dolgoztam. 
 - Megfogalmaztam a funkcionális követelményeket
- Létrehoztam az adatmodellt, melyhez diagramot is készítettem
- Leírtam az OAuth2 azonosítás folyamatát, architektúráját
- Végiggondoltam a megvalósítás lehetőségeit

A kérdéseimet a Specifikációban piros színnel emeltem ki.

Emellett elkezdtem az autorizációs szerver implementálását. Ehhez elkészítettem az adatmodellhez illeszkedő adatbázis sémát MySql 8 adatbázissal. Létrehoztam egy Spring Boot projektet, és kapcsolódtam az adatbázishoz. Flyway segítségével biztosított az adatmigráció (db inicializálás), így más gépről is kevés beállítással futtatható.
Az entitások és a repository-k váza is elkészült.
Továbbá egy kezdetleges Security beállítást is hozzáadtam, ami jelenleg az automatikusan generált login formmal működik, postman-nel kipróbálható.
A klienseket már az adatbázisból éri el, azonban a felhasználók még csak inmemory vannak.
A token-t is még személyre kell szabni.

# 09.24.
## Ezen a héten a következőket valósítottam meg:
A héten az AS-rel foglalkoztam, sok mindent kiderítettem, nagyon sokat debuggoltam.
Felfedeztem, hogy működik rajta az összes openid endpoint. A jelenlegi beállítás is megtekinthető: 
![](well-known-endpoint1.png)

Először sikerült PostMan-nel megvalósítani, hogy működjön a Client Secret Basic azonosítás.

Ezután kitaláltam egy módszert a bejelentkezés egy másik személy nevében lehetőségre, ekkor a kliens extra paraméterben kéri az imitálni kívánt felhasználót (mimic: username), PostMan-nel ez is működött.

Végül létrehoztam az Angular frontend klienst. Eközben azt az ajánlást olvastam, hogy SPA klienseknél ez a metódus nem javasolt, ehelyett a Public client metódust kell használni. Találtam egy megfelelő könyvtárat, mellyel könnyen megvalósítható a kliens oldal: angular-auth-oidc-client. Hosszas próbálkozás után végre sikerült a bejelentkezést megvalósítani. (igaz még csak egy gomb, de működik)

Tehát a jelenlegi státusz: kezdetlegesen működik az auth server és a kliens be tud jelentkezni. Van rendes mysql db a szerver mögött, és azt is használja, az access token személyre szabott, visszaadja az azonosított felhasználó összes (publikus) adatát. Emellett néhány külön endpoint van még mint resource a szerveren, ami majd az adminoknak lesz elérhető.

# 10.01.
## Ezen a héten a következőket valósítottam meg:
### Authorizáció update:
- van saját személyre szabott userinfo protocol endpoint
- a megszemélyesítés act_as paraméterrel megadható, ehhez megfelelő tokent ad vissza
- plusz csináltam email service-t :) ez regisztráció után email validációs linket küld, és csak ezzel válik sikeressé a regisztráció
- csináltam néhány (cseppet sem kimerítő) tesztet

### Regisztrációs oldal:
- saját Bootstrap 5-ös login és regisztrációs oldalakat készítettem ThymeLeaf template-ek segítségével és ehhez megfelelő kontrollereket
- alapvető input ellenőrzés és hibakezelés: kötelező mezők kitöltése, username egyedi legyen
- külön visszajelzés sikeres és sikertelen regisztráció esetén
  
### Frontend:
- Angular Material elemeket felhasználva elkészítettem az alkalmazás vázát: module, routing, néhány (üres) oldal
- elkészült a login komponens, hozzá tartozó szerviz, login után megjelenik a profil ikon, a userinfo
- logout működik
- login után, ha a szerepkör megengedi, a megszemélyesítés opció megjelenik, és a kiválasztott névre klikkelve automatikusan átjelentkezik abba a profilba
- hozzáadtam egy interceptort, ez az access tokent felteszi minden kérésre
- van egy mindenhonnan elérhető UserInfo model osztály, ami a bejelentkezés eseményét publikálja, és az aktuális user adatait elérhetővé teszi

