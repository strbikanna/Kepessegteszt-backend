import {Injectable} from '@angular/core';
import {map, Observable, of} from "rxjs";
import {RecommendedGame} from "../../model/recommended_game.model";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {User} from "../../model/user.model";
import {Game} from "../../model/game.model";
import {UserInfo} from "../../auth/userInfo";
import {HttpClient} from "@angular/common/http";

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
        return of()
    }

    /**
     * Returns recommended game made by backend.
     */
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
        return of()
    }

    getAllGames(): Observable<RecommendedGame[]> {
        return of()
    }
}
