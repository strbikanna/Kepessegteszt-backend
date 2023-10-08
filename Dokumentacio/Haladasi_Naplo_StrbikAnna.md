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

# 10.08.
## Ezen a héten a következőket valósítottam meg:

### Frontend
- kicsit szebb
- van külön oldal a játékoknak: itt listázva megjelennek külön tabfüleken a tanár/kutató/rendszer által ajánlott játékok és az összes játék
- egyelőre mockolt adatokkal működik, de a data flow már teljesen ki van alakítva
- lokalizáltam a sztringeket, így egy helyen van gyűjtve az összes üzenet, amit az alkalmazás megjelenít
- sikerült integrálni a játékokat, beillesztettem Csaba szkriptjét, és az elkészült játékokat az Angular projektbe: egy külön komponens jeleníti meg, külön rout-on érhető el
- sikerült összekötni a mockolt adatokkal, és így paraméterezetten elindítani a játékokat, hozzáadtam egyszerű hibakezelést is

### Backend - Resource server
- a felhasználókkal és profillal kapcsolatos funkciókon dolgoztam
- létrehoztam a resource server projektet, h2 in memory és mysql adatbázisokkal tesztelésre/fejlesztésre külön profilokkal
- felállítottam flyway data migrationt
- létrehoztam az adatmodellt leíró SQL szkriptet, ezt futtatja a flyway
- újragondoltam az adatmodellt, és változtattam néhány dolgon, szerintem így rugalmasabb lesz
- létrehoztam az adatmodellnek megfelelő Entity osztályokat és a köztük lévő kapcsolatokat
- létrehoztam repository-kat, service osztályokat
- készítettem külön DTO osztályokat is a user-hez, a PlainUserDto nem tartalmazza a profilt, míg a UserProfileDto igen
- írtam teszteket a repository-khoz és a service-ekhez, ezek alapján javítottam a hibákat
- létrehoztam a UserController-t, egyelőre csak két metódussal (nem láttam értelmét többnek): GET -> saját profil, PUT -> adminjoggal
- megírtam a security releváns részeit is, ezt azonban egyelőre "kikapcsoltam" a dev és test profilon, mert még nem tudtam tesztelni
- (a controller-ben már kell autentikáció, de ez még nem fog működni az inaktív security miatt)

### Kérdéseim
- most van a resource serveren Role entitás, hozzákapcsolva a User-hez, de szerintem erre nincs szükség, törölhetem?
- gondolkoztam azon, hogyan lehetne minél egyszerűbben megoldani, hogy a képesség különféle típusú lehessen, javaslataim:
 - varchar-ként tárolni az abilityValue-t és felvenni egy abilityType mezőt az Ability-be, ami megadja, hogyan kell értelmezni
 - a ProfileItem-nek lehetnek speciális leszármazottai, különböző típusokkal (de akkor ezeket külön táblán kell tárolni, és egyben a ProfileSnapshotItemnek is lesznek ugyanilyen leszármazottai)
 - lehet többféle abilityValue field-je a ProfileItemnek, és nem mind van kitöltve, ennek megfelelően több nullable oszlopa lenne a táblának is...
- nyilván mindnek van hátránya, nekem most ezek jutottak eszembe, a legjobb megoldás szerintem attól is nagyban függ, hogy a gyakorlatban mennyire lehet sokféle value típusra számítani

