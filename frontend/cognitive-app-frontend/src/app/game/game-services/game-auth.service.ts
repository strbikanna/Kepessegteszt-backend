import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {RecommendedGame} from "../../model/recommended_game.model";
import {AuthOptions, LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {GameInfo} from "../../auth/gameInfo";
import {UserInfo} from "../../auth/userInfo";
import {AppConstants} from "../../utils/constants";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

/**
 * Service that retrieves game access token for the chosen game
 * And helps with data transfer between sibling components: game-page and playground
 */
@Injectable({
    providedIn: 'root'
})
export class GameAuthService {
    public chosenGame = new BehaviorSubject<RecommendedGame | undefined>(undefined);

    private GAME_CONFIG_ID = 'gameTokenConfig'
    private readonly sessionStoreKey = 'playground_chosengame'


    constructor(private oidcSecurityService: OidcSecurityService, private http: HttpClient, private router: Router) {
    }

    /**
     * Handle game config callback with auth code
     */

    initAuthentication() {
        this.oidcSecurityService
            .checkAuth(undefined, this.GAME_CONFIG_ID)
            .subscribe((loginResponse: LoginResponse) => {
                    console.log('Game authentication happened: ' + loginResponse.isAuthenticated)
                    GameInfo.accessToken = loginResponse.accessToken
                    GameInfo.authStatus.next(loginResponse.isAuthenticated)
                }
            );
    }

    /**
     * Authorizes chosen game for logged-in user
     * @param id
     */
    getGameToken(id: number) {
        GameInfo.currentGameId = id
        GameInfo.authStatus.next(false)
        const isImpersonation = sessionStorage.getItem(AppConstants.impersonationKey)
        let customParams: AuthOptions = {};
        if (isImpersonation) {
            customParams = {customParams: {game_id: id, act_as: UserInfo.currentUser.username}}
            console.log('Authorizing impersonation user: ' + UserInfo.currentUser.username)
        } else {
            customParams = {customParams: {game_id: id}}
        }
        this.oidcSecurityService.authorize(this.GAME_CONFIG_ID, customParams)
    }

    /**
     * Sends chosen game to playground with proper config params and navigates to playground
     * @param recommendation
     */

    publishChosenGame(recommendation: RecommendedGame) {
        recommendation.config.username = UserInfo.currentUser?.username ?? 'unknown'
        recommendation.config.access_token = GameInfo.accessToken

        this.chosenGame.next(recommendation)
        this.saveChosenGame(recommendation)
        this.router.navigate(['/playground'],)

    }

    /**
     * Retrieves chosen game from session storage if present
     */
    tryLoadChosenGame() {
        const chosenGame = sessionStorage.getItem(this.sessionStoreKey)
        if (chosenGame !== null) {
            this.chosenGame.next(JSON.parse(chosenGame))
        }
    }

    private saveChosenGame(game: RecommendedGame) {
        sessionStorage.setItem(this.sessionStoreKey, JSON.stringify(game))
    }

}
