/**
 * Texts displayed in application components.
 */
export const TEXTS = {
    menu: {
        login: 'Bejelentkezés',
        logout: 'Kijelentkezés',
        home: 'Cognitive App',
        games: 'Játékok',
        profile: 'Profil',
        cognitive_profile: 'Kognitív profil',
        profile_comparison: 'Kognitív profil elemzés',
        user_management: 'Felhasználók kezelése',
        game_management: 'Játékok kezelése',
        result_management: 'Eredmények',
        game_recommendation: 'Játékok ajánlása',
    },
    home: {
        welcome: 'Örülünk, hogy itt vagy!',
        features_student: 'Játssz a kedvenc játékaiddal és kövesd nyomon a profilod fejlődését!',
        features_parent: 'Kövesd nyomon gyermeked fejlődését és segíts neki a játékok kiválasztásában!',
        features_teacher: 'Kövesd nyomon diákjaid fejlődését és ajánlj nekik játékokat!',
        features_scientist: 'Tölts fel játékokat, ajánld őket diákoknak és kövesd nyomon a fejlődésüket!',
        login: 'Jelentkezz be a játékok eléréséhez!',
    },
    impersonation: {
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
        game_card: {
            play: 'Játék indítása',
        },
        progress: {
            loading_game: 'Játék betöltése...',
        },
        error: {
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
        error: {
            title: 'Hiba történt',
            explanation: 'Valószínűleg újratöltötted a játékot vagy megszakadt az internetkapcsolat.' +
                'Sajnos az előző játékot nem tudod folytatni, térj vissza a játékok oldalra és válassz egy új játékot.',
            action: 'Vissza a játékokhoz',
        }
    },
    paging: {
        firstPage: 'Első oldal',
        itemsPerPage: 'Találatok száma oldalanként',
        lastPage: 'Utolsó oldal',
        nextPage: 'Következő oldal',
        previousPage: 'Előző oldal',
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
        actions: {
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
    error: {
        default_error_message: 'Ismeretlen hiba történt',
        default_error_title: 'Hiba történt',
        http_error: 'Hiba történt a szerverrel való kommunikáció során.',
        http_error_details: 'A szerver válasza: ',
        invalide_start_date: 'Érvénytelen kezdődátum',
        invalide_end_date: 'Érvénytelen végdátum',
        empty_content: 'Nincs megjeleníthető tartalom',
        network_error: 'Valószínűleg nincs internetkapcsolat.',
        unauthorized_error: 'Nincs jogosultságod a kért művelet végrehajtásához.',
        invalid_values: 'A megadott értékek nem megfelelőek.',
        invalid_param_order: 'Ez a sorszám már létezik.',
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
        radar_chart: {
            chart_title: 'Kognitív profil összehasonlítása',
            profile: 'Saját profil',
            avg_in_group: 'Átlagos értékek a csoportban',
            max_in_group: 'Maximumális értékek a csoportban',
            min_in_group: 'Minimum értékek a csoportban',
        },
        card: {
            current_profile: 'Legfrissebb kognitív profil',
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
        comparison: {
            title: 'Kognitív profil elemzése',
            description: 'Válaszd ki, mely csoport adatait szeretnéd összehasonlítani a sajátoddal',
            avg: 'Átlag',
            min: 'Minimum',
            max: 'Maximum',
            comparison_type: 'Összehasonlítás típusa',
            comparison_type_description: 'Válaszd ki, milyen módon legyenek megjelenítve a csoport adatai',
            group_selection: 'Csoport kiválasztása',
            submit: 'Összehasonlítás',
            error_group_required: 'Válassz ki egy csoportot az összehasonlításhoz',
            error_type_required: 'Válassz ki egy összehasonlítási típust',
            error_no_group: 'Úgy látszik, egyetlen csoportnak sem vagy még tagja'
        },
    },

    wildcard: {
        title: 'Hupsz, ez az oldal nem található!',
        description: 'A keresett oldal nem található. Ellenőrizd a címet és próbáld újra.',
    },
    game_management: {
        title: 'Játékok kezelése',
        calculation_dialog: {
            title: 'Játékeredmények kiértékelése',
            description: 'A játékeredmények kiértékelése magában foglalja az összes eddig összegyűlt játékeredmény feldolgozását. A játékok pontszámai normalizálva lesznek, és az eredmények alapján a felhasználók képességértékei frissítve lesznek. A kiértékelést nem lehet visszavonni.',
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
            confirm: {
                title: 'Biztosan mented a játékot?',
                message: 'Megváltozott a játékkonfiguráció. Az előző játék így inaktív lesz és egy új játék lesz létrehozva a megadott adatokkal. A mentés után az új játéknak új ID-ja lesz!'
            }
        },
        config_item_form: {
            title: 'Konfigurációs elem adatai',
            param_name: 'Paraméter neve',
            initial_value: 'Kezdőérték',
            hardest_value: 'Legnehezebb érték',
            easiest_value: 'Legkönnyebb érték',
            increment: 'Léptetés',
            param_order: 'Paraméter sorszáma',
            param_order_description: 'Azt fejezi ki, hogy mennyire befolyásolja a játékbeállítást, minél kisebb a szám, annál relevánsabb a paraméter.',
            description: 'Leírás',
            description_placeholder: 'Opcionális leírás a paraméterről',
        }
    },
    file_upload: {
        no_content: 'Nincs kiválasztott fájl',
    },
    recommendation_page: {
        user_data: {
            first_name: 'Keresztnév',
            last_name: 'Vezetéknév',
            already_recommended: 'Korábban ajánlott játékok',
            not_yet_recommended: 'Még nem ajánlott játékok'
        },
        actions: {
            cancel: 'Mégse',
            save: 'Mentés',
            recommend_game: 'Játék ajánlása',
            remove: 'Ajánlás törlése',
            add: 'Hozzáadás',
        },
        rec_game_data: {
            name: 'Játék neve: ',
            time: 'Ajánlás ideje: ',
            completed: 'Ajánlás állapota: ',
            config: 'Ajánlott szint: '
        }
    },
    result: {
        result_info: {
            game_name: 'Játék neve: ',
            timestamp: 'Játék időpontja',
            result: 'Eredmény ',
            passed: 'Sikeres',
            failed: 'Sikertelen',
            config: 'Játék konfiguráció',
            result_detailed: 'Játék eredmény részletei',
            username: 'Játékos'
        }
    }
}