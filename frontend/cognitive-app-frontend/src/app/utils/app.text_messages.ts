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
    contact_autocomplete: {
        label: 'Kapcsolatok',
        hint: 'Kapcsolat hozzáadása',
        placeholder: 'Új kapcsolat',
        buttonText: 'Hozzáadás',
    },
    user_autocomplete: {
        label: 'Felhasználók',
        hint: 'Felhasználó kiválasztása',
        placeholder: 'Felhasználó neve',
    },
    game_auto_complete: {
        label: 'Játékok',
        hint: 'Játék kiválasztása',
        placeholder: 'Játék neve',
        buttonText: 'Kiválasztás',
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
        check_all: 'Összes kijelölése',
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
        user_picker: {
            choose_contact: 'Kognitív profil megtekintése',
            choose_contact_description: 'Válaszd ki, kinek a kognitív profilját szeretnéd megtekinteni',
            select: 'Megtekintés',
        },
        date_picker: {
            see_history: 'Megtekintés korábbi időpontban',
            see_history_description: 'Válassz egy kezdő és egy végdátumot, hogy megtekinthesd a kognitív profilodat az adott időpontban.',
            see_history_action: 'Kognitív profil megtekintése',
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
            error_no_group: 'Úgy látszik, egyetlen csoportnak sem vagy még tagja',
            composite_filter_title: 'Felhasználók részletes szűrése',
            composite_filter_description: 'Kontrollcsoport kijelölése több szűrő használatával',
            data_own: 'Saját kognitív profil érték',
            data_group: 'Csoport összesített értéke',
        },
        user_filter: {
            title: 'Felhasználók szűrése',
            description: 'Válaszd ki, milyen kritériumok alapján szeretnéd kiválasztani az összehasonlítás alapjául szolgáló felhasználói csoportot.',
            age: 'Életkor',
            age_min: 'Minimum kor',
            age_max: 'Maximum kor',
            ability: 'Kognitív képesség',
            ability_min: 'Minimum érték',
            ability_max: 'Maximum érték',
            group: 'Felhasználói csoport',
            group_selection: 'Csoport kiválasztása',
            address: 'Lakóhely',
            address_city: 'Város',
            address_zip: 'Irányítószám',
            submit: 'Szűrés',
        },
    },
    date_picker: {
        time_interval: 'Időintervallum',
        title: 'Megtekintés korábbi időpontban',
        description: 'Válassz egy kezdő és egy végdátumot, hogy megtekinthesd a kognitív profilodat az adott időpontban.',
        action: 'Kognitív profil megtekintése',
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
    csv_download: {
        download_info: 'Adatok letöltése CSV fájlként.',
    },
    recommendation_page: {
        title: 'Játékajánlás',
        subtitle: 'Játékbeállítás készítése egy felhasználó számára. Válaszd ki a felhasználót és a játékot, majd állítsd be a játékparamétereket.',
        user_autocomplete: {
            label: 'Felhasználó',
            hint: 'Felhasználó kiválasztása',
            placeholder: 'Felhasználó neve',
            buttonText: 'Kiválasztás',
        },
        error: {
            no_user_selected: 'Válassz ki egy felhasználót a játékajánláshoz',
            no_game_selected: 'Válassz ki egy játékot a játékajánláshoz',
            invalid_param: 'A paraméter értéke nem megfelelő.',
        },
        submit: 'Ajánlás létrehozása',
        chosenUser: 'Kiválasztott játékos',
        chosenGame: 'Kiválasztott játék',
        saved: 'Ajánlás mentve',
        existing_recommendations: 'Meglévő ajánlások',
    },
    result: {
        result_info: {
            game: 'Játék neve ',
            timestamp: 'Játék időpontja',
            result: 'Eredmény ',
            passed: 'Sikeres',
            failed: 'Sikertelen',
            config: 'Játék konfiguráció',
            result_detailed: 'Játék eredmény részletei',
            username: 'Játékos'
        },
        sort: "Rendezés",
        filter: "Szűrés",
        filter_and_sort: "Szűrés és rendezés",
        username: "Felhasználónév alapján",
        game: "Játék alapján",
        passed: "Játékeredmény alapján",
        timestamp: "Időpont alapján",
        apply: "Kiválasztás alkalmazása",

    }
}