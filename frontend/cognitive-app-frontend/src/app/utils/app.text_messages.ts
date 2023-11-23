export const TEXTS = {
    menu: {
        login: 'Bejelentkezés',
        logout: 'Kijelentkezés',
        home: 'Cognitive App',
        games: 'Játékok',
        profile: 'Profil',
        cognitive_profile: 'Kognitív profil',
        admin: 'Felhasználók kezelése',
        game_management: 'Játékok kezelése',
    },
    home:{
        welcome: 'Örülünk, hogy itt vagy!',
        features_student: 'Játssz a kedvenc játékaiddal és kövesd nyomon a profilod fejlődését!',
        features_parent: 'Kövesd nyomon gyermeked fejlődését és segíts neki a játékok kiválasztásában!',
        features_teacher: 'Kövesd nyomon diákjaid fejlődését és ajánlj nekik játékokat!',
        features_scientist: 'Tölts fel játékokat, ajánld őket diákoknak és kövesd nyomon a fejlődésüket!',
        login: 'Jelentkezz be a játékok eléréséhez!',
    },
    impersonation:{
        message: 'Bejelentkezhetsz a következő felhasználók nevében, vagy maradhatsz a saját fiókodban.',
        cancel: 'Maradok a saját fiókomban',
    },
    games: {
        menu: {
            teacher_recommendation: 'Tanári ajánlások',
            scientist_recommendation: 'Kutatók ajánlásai',
            games_for_you: 'Játékok neked',
            all_games: 'Összes játék',
        },
        game_card:{
            play: 'Játék indítása',
        },
        progress:{
            loading_game: 'Játék betöltése...',
        },
        error:{
            empty_content: 'Még nem érkezett ajánlás',
        }
    },
    user_info: {
        description: 'Felhasználói profil információk',
        first_name: 'Keresztnév',
        last_name: 'Vezetéknév',
        email: 'Email-cím',
        username: 'Felhasználónév',
        roles: 'Szerepkörök',
    },
    playground: {
        error:{
            title: 'Hiba történt',
            explanation: 'Valószínűleg újratöltötted a játékot vagy megszakadt az internetkapcsolat.' +
                'Sajnos az előző játékot nem tudod folytatni, térj vissza a játékok oldalra és válassz egy új játékot.',
            action: 'Vissza a játékokhoz',
        }
    },
    paging:{
        firstPage : 'Első oldal',
        itemsPerPage : 'Találatok száma oldalanként',
        lastPage : 'Utolsó oldal',
        nextPage : 'Következő oldal',
        previousPage : 'Előző oldal',
    },
    admin_page: {
        user_data: {
            first_name: 'Keresztnév',
            last_name: 'Vezetéknév',
            email: 'E-mail',
            username: 'Felhasználónév',
            roles: 'Szerepkörök',
            contacts: 'Kapcsolatok',
        },
        actions:{
            cancel: 'Mégse',
            save: 'Mentés',
            add_contact: 'Kapcsolat hozzáadása',
            add: 'Hozzáadás',
            role_request: 'Igényelt szerpkörök',
            ok: 'OK',
        },
        update_success_message: 'A felhasználó adatai sikeresen frissítve lettek',
        update_error_message: 'A felhasználó adatainak frissítése sikertelen volt',
    },
    error:{
        default_error_message: 'Ismeretlen hiba történt',
        default_error_title: 'Hiba történt',
        http_error: 'Hiba történt a szerverrel való kommunikáció során',
        http_error_details: 'A szerver válasza: ',
        invalide_start_date: 'Érvénytelen kezdődátum',
        invalide_end_date: 'Érvénytelen végdátum',
        empty_content: 'Nincs megjeleníthető tartalom',
    },
    actions: {
        cancel: 'Mégse',
        save: 'Mentés',
        ok: 'OK',
        edit: 'Szerkesztés',
        calculation: 'Kiértékelés',
        delete: 'Törlés',
        upload: 'Feltöltés',
    },
    cognitive_profile: {
        chart: {
            chart_title: 'Kognitív profil az elmúlt időszakban',
        },
        card: {
            current_profile: 'Legfrisebb kognitív profil',
            current_profile_description: 'A kognitív profilban szereplő képességek, és értékük ebben az időpontban ',
            no_profile_data_message: 'Nincs elég adat, ezért nem lehet megjeleníteni a kognitív profilt.',
            no_profile_data_action: 'Több játék játszása esetén összeáll a kognitív profil.',
            abilities: 'Kognitív képességek',
            values: 'Becsült értékek',
        },
        date_picker: {
            show_profile_history: 'Kognitív profil előzmények megtekintése',
            time_interval: 'Időintervallum',
            see_history: 'Megtekintés korábbi időpontban',
            see_history_description: 'Válassz egy kezdő és egy végdátumot, hogy megtekinthesd a kognitív profilodat az adott időpontban.',
            see_history_action: 'Kognitív profil megtekintése',
            back_to_current_action: 'Vissza az aktuális kognitív profilhoz',
        },
        user_picker: {
            choose_contact: 'Kognitív profil megtekintése',
            choose_contact_description: 'Válaszd ki, kinek a kognitív profilját szeretnéd megtekinteni',
            select: 'Megtekintés',
        },
    },

    wildcard:{
        title: 'Hupsz, ez az oldal nem található!',
        description: 'A keresett oldal nem található. Ellenőrizd a címet és próbáld újra.',
    },
    game_management:{
        title: 'Játékok kezelése',
        calculation_dialog:{
            title: 'Játékeredmények kiértékelése',
            description: 'A játékeredmények kiértékelése magában foglalja az összes eddig összegyűlt játékeredmény feldolgozását. ' +
                'A játékok pontszámai normalizálva lesznek, és az eredmények alapján a felhasználók képességértékei frissítve lesznek. A kiértékelést nem lehet visszavonni.',
            message: 'Biztosan szeretnéd elindítani a játékeredmények kiértékelését?',
            result_count: 'A játékhoz érkezett feldolgozatlan eredmények száma: ',
            affected_abilities: 'A játék ezeket a kognitív képességeket vizsgálja: ',
            no_abilities: 'A játék nem vizsgál kognitív képességeket.',
            feedback_message: 'A kiértékelés elkezdődött, a folyamat néhány percig eltarthat.',
            success_feedback: 'A kiértékelés sikeresen befejeződött.',
            result_mean: 'A normalizálás során kapott átlag: ',
            result_deviation: 'A normalizálás során kapott szórás: ',
            result_profile_count: 'A normalizálás után frissített felhasználói profilok száma: ',
        },
        edit_form: {
            title: 'Játék adatainak szerkesztése',
            data_section_title: 'Játék adatai',
            name: 'Név',
            description: 'Leírás',
            thumbnail: 'Borítókép',
            url: 'URL',
            active: 'Aktív ',
            non_active: 'Nem Aktív ',
            version: 'Verzió ',
            config_description: 'Játékkonfiguráció leírása',
            affected_abilities: 'Mért kognitív képességek',
            value: 'Érték',
            config_error_message: 'Ezek a karakterek használhatók: "a-z", "A-Z", "0-9", "-", "_"',
        }
    },
    file_upload: {
        no_content: 'Nincs kiválasztott fájl',
    }

}