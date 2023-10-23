import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {GameplayModel} from "../../model/gameplay.model";

@Injectable({
    providedIn: 'root'
})
export class GameService {

    constructor(private http: HttpClient) {
    }

    getTeacherRecommendedGames(): Observable<GameplayModel[]> {
        return this.mockData
    }

    getGamesForCurrentUser(): Observable<GameplayModel[]> {
        return this.mockData
    }

    getScientistRecommendedGames(): Observable<GameplayModel[]> {
        return this.mockData
    }

    getAllGames(): Observable<GameplayModel[]> {
        return this.mockData
    }

    private mockData: Observable<GameplayModel[]> = of(
        [
            {
                id: "balloon001-223-554",
                name: "Pop the balloons",
                description: "Try not to die from the bombs.",
                thumbnail: "../../assets/balloon_game.jpg",
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
                config: {
                    game_id: 2,
                    gameName: "number-repeating",
                    gameTitle: "Rock The Radio",
                    level: 2
                }
            },
            {
                id: "cosmic-sec-001-123-504",
                name: "Cosmic sequence",
                description: "Enjoy space life.",
                thumbnail: "../../assets/cosmic.jpg",
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
