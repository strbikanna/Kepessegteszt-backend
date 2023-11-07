import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {filter, map, Observable, of} from "rxjs";
import {Game} from "../../model/game.model";
import {AppConstants} from "../../utils/constants";

/**
 * Service that calls backend for game data.
 */
@Injectable({
    providedIn: 'root'
})
export class GameService {
    private readonly url = `${AppConstants.resourceServerUrl}/gameplay/all/system_recommended`

    constructor(private http: HttpClient) {}

    getTeacherRecommendedGames(): Observable<Game[]> {
        return this.mockData
    }

    getGamesForCurrentUser(): Observable<Game[]> {
        return this.http.get<Game[]>(this.url).pipe(
            map(games => {
                console.log(games)
                return games
            })
        )
    }

    getScientistRecommendedGames(): Observable<Game[]> {
        return this.mockData
    }

    getAllGames(): Observable<Game[]> {
        return this.mockData
    }

    private mockData: Observable<Game[]> = of(
        [
            {
                id: "balloon001-223-554",
                name: "Pop the balloons",
                description: "Try not to die from the bombs.",
                thumbnail: "../../assets/balloon_game.jpg",
                url: undefined,
                config: {
                    game_id: 1,
                    gameName: "balloon-pop",
                    gameTitle: "Pop The Balloon",
                    level: 1
                },
            },
            {
                id: "radio-num-001-223-554",
                name: "Radio buttons",
                description: "Push the correct buttons of the secret radio.",
                thumbnail: "../../assets/number_game.jpg",
                url: undefined,
                config: {
                    game_id: 2,
                    gameName: "number-repeating",
                    gameTitle: "Rock The Radio",
                    level: 1
                }
            },
            {
                id: "cosmic-sec-001-123-504",
                name: "Cosmic sequence",
                description: "Enjoy space life.",
                thumbnail: "../../assets/cosmic.jpg",
                url: undefined,
                config: {
                    game_id: 3,
                    gameName: "cosmic-sequence",
                    gameTitle: "Are you a space cadet?",
                    level: 2
                },
            }
        ]
    )
}
