import {Component} from '@angular/core';
import {BehaviorSubject, Observable, of, tap} from "rxjs";
import {Result} from "../../../model/result.model";
import {ResultService, SearchOptions} from "../../../service/result/result.service";
import {GameManagementService} from "../../../service/game-management/game-management.service";
import {ConfigItem} from "../../../model/config_item.model";

@Component({
    selector: 'app-result-insight-page',
    templateUrl: './result-insight-page.component.html',
    styleUrl: './result-insight-page.component.scss'
})
export class ResultInsightPageComponent {
    private searchOptions: SearchOptions = {
        sortBy: 'timestamp',
        sortOrder: 'DESC',
        pageIndex: 0,
        pageSize: 5,
    }
    resultData: BehaviorSubject<Result[]> = new BehaviorSubject<Result[]>([]);
    configItems: BehaviorSubject<ConfigItem[]> = new BehaviorSubject<ConfigItem[]>([]);

    constructor(private resultService: ResultService, private gameService: GameManagementService) {
    }

    onUserSelected(username: string) {
        this.searchOptions.usernames = [username]
        this.searchOptions.pageIndex = 0
        this.onGetResults()
    }

    onUserRemoved() {
        this.searchOptions.usernames = []
        this.searchOptions.pageIndex = 0
    }

    onGameSelected(gameId?: number) {
        if (gameId === undefined) {
            this.searchOptions.gameIds = []
            return
        }
        this.searchOptions.gameIds = [gameId]
        this.searchOptions.pageIndex = 0
        this.onGetResults()
    }

    onNextPage() {
        this.searchOptions.pageIndex++
        this.onGetResults()
    }

    onPreviousPage() {
        this.searchOptions.pageIndex--
        if (this.searchOptions.pageIndex < 0) {
            this.searchOptions.pageIndex = 0
        }
        this.onGetResults()
    }

    onGetResults() {
        if (!this.searchOptions.usernames || this.searchOptions.usernames.length === 0) {
            return
        }
        if (!this.searchOptions.gameIds || this.searchOptions.gameIds.length === 0) {
            return
        }
        this.resultService.getAllResultsFiltered(this.searchOptions).subscribe(results => {
            this.resultData.next(results)
        })
        this.gameService.getGameById(this.searchOptions.gameIds[0]).subscribe(game => {
            this.configItems.next(game.configItems)
        })
    }


    mockData: Observable<Result[]> = of([
        {
            id: 2323,
            timestamp: new Date("2025-03-03T11:55:46"),
            result: {
                "time": 3758,
                "passed": false
            },
            config: {
                rounds: 8,
                bug_spots: 3,
                game_mode: 0,
                food_count: 7,
                time_limit_millis: 5000
            },
            gameId: 31,
            gameName: "Űrmálna Etetés (Blokkok)",
            username: "student_user",
            passed: false
        },
        {
            id: 2211,
            timestamp: new Date("2025-02-22T13:02:46"),
            result: {
                "passed": true,
                "results": [
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 994,
                        "touchReleasedMillis": 381
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 986,
                        "touchReleasedMillis": 478
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 860,
                        "touchReleasedMillis": 351
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 960,
                        "touchReleasedMillis": 460
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 887,
                        "touchReleasedMillis": 379
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 997,
                        "touchReleasedMillis": 418
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 1238,
                        "touchReleasedMillis": 520
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 1442,
                        "touchReleasedMillis": 424
                    }
                ]
            },
            config: {
                rounds: 8,
                bug_spots: 3,
                game_mode: 1,
                food_count: 8,
                time_limit_millis: 4000
            },
            gameId: 31,
            gameName: "Űrmálna Etetés (Blokkok)",
            username: "student_user",
            passed: true
        },
        {
            id: 2187,
            timestamp: new Date("2025-02-20T15:09:01"),
            result: {
                "time": 33117,
                "passed": false
            },
            config: {
                rounds: 9,
                bug_spots: 2,
                game_mode: 1,
                food_count: 7,
                time_limit_millis: 5000
            },
            gameId: 31,
            gameName: "Űrmálna Etetés (Blokkok)",
            username: "student_user",
            passed: false
        },
        {
            id: 1336,
            timestamp: new Date("2024-08-02T11:28:10"),
            result: {
                passed: true,
                results: [
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 795,
                        "touchReleasedMillis": 398
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 626,
                        "touchReleasedMillis": 352
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 679,
                        "touchReleasedMillis": 485
                    },
                    {
                        "isCorrect": true,
                        "bugTouchedMillis": 574,
                        "touchReleasedMillis": 364
                    }
                ]
            },
            config: {
                rounds: 8,
                bug_spots: 3,
                game_mode: 0,
                food_count: 6,
                time_limit_millis: 3000
            },
            gameId: 31,
            gameName: "Űrmálna Etetés (Blokkok)",
            username: "student_user",
            passed: true
        },
        {
            id: 1335,
            timestamp: new Date("2024-08-02T11:25:48"),
            result: {
                "time": 41603,
                "passed": false
            },
            config: {
                rounds: 9,
                bug_spots: 4,
                game_mode: 0,
                food_count: 5,
                time_limit_millis: 5000
            },
            gameId: 31,
            gameName: "Űrmálna Etetés (Blokkok)",
            username: "student_user",
            passed: true
        }
    ])

    mockConfigItems = of([
        {
            "id": 116,
            "paramName": "bug_spots",
            "maxAbilityEffect": 2.0,
            "easiestValue": 2,
            "hardestValue": 8,
            "initialValue": 2,
            "increment": 2,
            "description": "Mezők száma, ahol megjelenhetnek a bogarak."
        },
        {
            "id": 118,
            "paramName": "rounds",
            "maxAbilityEffect": 4.0,
            "easiestValue": 1,
            "hardestValue": 10,
            "initialValue": 1,
            "increment": 1,
            "description": "A körök száma egy játékmenetben.\n(NORMAL játékmódban nincs figyelembe véve.)"
        },
        {
            "id": 114,
            "paramName": "game_mode",
            "maxAbilityEffect": 1.0,
            "easiestValue": 0,
            "hardestValue": 3,
            "initialValue": 0,
            "increment": 1,
            "description": "Játékmód:\n0-> NORMAL,\n1-> INFINITE,\n2-> LEFT_HANDED,\n3-> RIGHT_HANDED"
        },
        {
            "id": 115,
            "paramName": "time_limit_millis",
            "maxAbilityEffect": 5.0,
            "easiestValue": 5000,
            "hardestValue": 1000,
            "initialValue": 5000,
            "increment": -1000,
            "description": "Idő hossz a választásra (ezredmásodpercben)."
        },
        {
            "id": 117,
            "paramName": "food_count",
            "maxAbilityEffect": 3.0,
            "easiestValue": 8,
            "hardestValue": 1,
            "initialValue": 8,
            "increment": -1,
            "description": "Hány málna közül tudjon választani a játékos.\n(LEFT_HANDED és RIGHT_HANDED játékmódban nincs figyelembe véve.)"
        }
    ])

}
