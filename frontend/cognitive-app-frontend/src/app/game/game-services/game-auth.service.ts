import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {GameplayModel} from "../../model/gameplay.model";
import {AuthOptions, LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {GameInfo} from "../../auth/gameInfo";
import {UserInfo} from "../../auth/userInfo";
import {AppConstants} from "../../utils/constants";

/**
 * Service that retrieves game access token for the chosen game
 * And helps with data transfer between sibling components: game-page and playground
 */
@Injectable({
    providedIn: 'root'
})
export class GameAuthService {
    public chosenGame = new BehaviorSubject<GameplayModel | undefined>(undefined);

    private GAME_CONFIG_ID = 'gameTokenConfig'

    constructor(private oidcSecurityService: OidcSecurityService) {
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

    publishChosenGame(game: GameplayModel) {
        game.config.username = UserInfo.currentUser?.username ?? 'unknown'
        console.log('User for game: ' + game.config.username)
        game.config.access_token = GameInfo.accessToken
        this.chosenGame.next(game)
    }

}
