export const TEXTS = {
    menu: {
        login: 'Bejelentkezés',
        logout: 'Kijelentkezés',
        home: 'Cognitive App',
        games: 'Játékok',
        profile: 'Profil',
        cognitive_profile: 'Kognitív profil',
        admin: 'Admin',
    },
    home:{
        welcome: 'Örülünk, hogy itt vagy a Cognitive Appban!',
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
        }
    },
    error:{
        default_error_message: 'Ismeretlen hiba történt',
        default_error_title: 'Hiba történt',
        http_error: 'Hiba történt a szerverrel való kommunikáció során',
        http_error_details: 'A szerver válasza: ',
        invalide_start_date: 'Érvénytelen kezdődátum',
        invalide_end_date: 'Érvénytelen végdátum',
    },
    actions: {
        cancel: 'Mégse',
        save: 'Mentés',
        ok: 'OK',
    },
    cognitive_profile:{
        chart:{
            chart_title: 'Kognitív profil az elmúlt időszakban',
        },
        card:{
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
        user_picker:{
            choose_contact: 'Kognitív profil megtekintése',
            choose_contact_description: 'Válaszd ki, kinek a kognitív profilját szeretnéd megtekinteni',
            select: 'Megtekintés',
        },

    }

}