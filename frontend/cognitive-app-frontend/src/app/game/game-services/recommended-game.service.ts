import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, of} from "rxjs";
import {AppConstants} from "../../utils/constants";
import {RecommendedGame} from "../../model/recommended_game.model";
import {SimpleHttpService} from "../../utils/simple-http.service";

/**
 * Service that calls backend for recommended game data.
 */
@Injectable({
    providedIn: 'root'
})
export class RecommendedGameService{
    private readonly path = '/recommended_game'
    private readonly systemRecommendedPath = '/system_recommended'
    constructor(private http: HttpClient, private helper: SimpleHttpService) { }

    getTeacherRecommendedGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }

    getGamesForCurrentUser(): Observable<RecommendedGame[]> {
        return this.http.get<RecommendedGame[]>(`${this.helper.baseUrl}${this.path}${this.systemRecommendedPath}`).pipe(
            map(recommendedGames =>
                recommendedGames.map(rg =>{
                    rg.config.game_id = rg.id
                    return rg
                })
            )
        )
    }

    getScientistRecommendedGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }

    getAllGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }

    private mockData: Observable<RecommendedGame[]> = of(
        [
            {
                id: "balloon-001-123-504",
                recommender: null,
                recommendedTo: null,
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 2,
                    name: "Pop the balloons",
                    description: "Try not to die from the bombs.",
                    thumbnail: "../../assets/balloon_game.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],

                },
                config: {
                    game_id: 2,
                    gameName: "balloon-pop",
                    gameTitle: "Pop The Balloon",
                    level: 1
                },
            },
            {
                id: "radio-001-123-504",
                recommender: null,
                recommendedTo: null,
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 1,
                    name: "Radio buttons",
                    description: "Push the correct buttons of the secret radio.",
                    thumbnail: "../../assets/number_game.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],
                },
                config: {
                    game_id: 1,
                    gameName: "number-repeating",
                    gameTitle: "Rock The Radio",
                    level: 1
                },
            },
            {
                id: "cosmic-sec-001-123-504",
                recommender: null,
                recommendedTo: null,
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 3,
                    name: "Cosmic sequence",
                    description: "Enjoy space life.",
                    thumbnail: "../../assets/cosmic.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],
                },
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
