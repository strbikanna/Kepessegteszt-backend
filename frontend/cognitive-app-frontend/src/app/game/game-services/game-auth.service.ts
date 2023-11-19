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

    getGameToken(id: number) {
        console.log('Trying to authorize with popup')
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

    publishChosenGame(recommendation: RecommendedGame) {
        recommendation.config.username = UserInfo.currentUser?.username ?? 'unknown'
        recommendation.config.access_token = GameInfo.accessToken

        if(recommendation.game.url !== undefined && recommendation.game.url !== null && recommendation.game.url !== '') {
            this.openGameUrl(recommendation)
            return
        }else{
            this.chosenGame.next(recommendation)
            this.saveChosenGame(recommendation)
            this.router.navigate(['/playground'],)
        }
    }

    tryLoadChosenGame(){
        const chosenGame = sessionStorage.getItem(this.sessionStoreKey)
        if(chosenGame !== null){
            this.chosenGame.next(JSON.parse(chosenGame))
        }
    }
    private saveChosenGame(game: RecommendedGame){
        sessionStorage.setItem(this.sessionStoreKey, JSON.stringify(game))
    }

    private openGameUrl(recommendedGame: RecommendedGame) {
        this.http.post(recommendedGame.game.url!, recommendedGame.config)
    }

}
