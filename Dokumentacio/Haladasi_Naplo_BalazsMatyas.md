# Haladási napló
## 09.18.
### Ezen a héten a következőkkel haladtam:

Annával átbeszéltük a specifikációt, ezen belül a különböző use caseket néztük meg, illetve a
tervezett technológiákat. Megbeszéltük, hogy melyikünk mivel foglalkozik a héten, ezek részemről első sorban
a vizuális tervek elkészítése a specifikációknál leírtak, valamint a hétfőn megbeszéltek alapján,
a rest api endpointok megtervezése, valamint az olvasnivalók elolvasása volt.

Ezek közül az olvasnivalókon kívül a vizuális terveket sikerült elkészítenem. Ezzel az eredetileg tervezettnél jelentősen
több időt sikerült eltöltenem, így sajnos a rest apira nem jutott időm, ezt a következő héten hétfőn vagy legkésőbb
kedden fogom bepótolni.

### A kiválasztott technológiák közül ami új lesz nekem:

Angular (teljesen új, még soha nem használtam)  
Kotlin (korábban csak nagyon kicsi egyetemi feladatban használtam)

## 09.25.

Annával átbeszéltük az ütemterv kapcsán, hogy melyik feladatot melyikünk fogja csinálni, ez az ütemterv doksiban látható.
A REST API endpointok megtervezését megcsináltam, valamint egy tervezett beosztást a saját feladataim kapcsán (minden hétre csak annyit osztottam be, amennyit
úgy gondoltam, hogy lesz rá időm). Ezen felül az Angularral kezdtem el ismerkedni, hogy majd amikor eljutok addig, hogy
a rám osztott oldalakat csináljam ne akkor lássam először.

## 10.02.
### Ezen a héten a következőkkel haladtam:

Angular tutorialokat nézegettem, egyelőre nem sikerült értelmes bármit alkotnom ilyen téren,
szóval azt nem is commitoltam. Egészen sokat vacakoltam vele, hogy IntelliJ-ben értelmesen lássák
az sql fájlok egymást/a másik fájlokban létrehozott táblákat. Megcsináltam a játék, játékmenet
és ajánlott játéktáblákat (az sql fájlokat hozzájuk), pár mock adat betöltését is megírtam,
de tesztelésig nem jutottam el (kódból sql-ezni újdonság nekem, szóval ezt is tanulnom kell).
A játékokhoz kapcsolódó REST API endpointokat felvázoltam, a működésüket még nem implementáltam.

### Amit következőnek meg kell csinálnom:

A betervezett kezdőoldal, valamint a megcsinált táblákhoz kapcsolódó Kotlinos dolgok (például a
REST API endpointok implementálása). Ezen felül a teszteket is meg kell írnom a játékokhoz kötődő
táblákhoz.

## 10.09.

Ezen a héten nem tudtam az önlabbal haladni.

## 10.16.

Egészen sok időt töltöttem a Kotlinos sql-ezés megértésével (nem állítom, hogy teljes mértékben sikerült,
de előrébb vagyok, mint voltam, plusz múltkor nem egészen jól voltak beállítva a dolgaim, szóval most
azt is meg kellett csinálnom). A Game-hez csináltam meg a CRUD műveletekhez szükséges API endpointokat.
A Recommended_game-hez az Entityt kiegészítettem azzal, ami az adatmodell alapján hiányzott belőle
(ezt szintén megtettem az sql leíró fájljában is), valamint a 2 endpointot amit Annával megbeszéltünk.
Ezeken felül írtam a GameRepositoryhoz és a RecommendedGameRepositoryhoz is néhány tesztet