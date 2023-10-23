import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {GameplayModel} from "../../model/gameplay.model";
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {GameInfo} from "../../utils/GameInfo";
import {UserInfo} from "../../utils/userInfo";

@Injectable({
    providedIn: 'root'
})
export class GameDataService {
    public chosenGame = new BehaviorSubject<GameplayModel | undefined>(undefined);

    constructor(private oidcSecurityService: OidcSecurityService){}

    initAuthentication(){
        this.oidcSecurityService.checkAuth(undefined, 'gameTokenConfig').subscribe((loginResponse: LoginResponse) => {
                console.log('Game authentication happened: ' + loginResponse.isAuthenticated)
                GameInfo.authStatus.next(loginResponse.isAuthenticated)
                GameInfo.accessToken = loginResponse.accessToken
                return
            }
        );
    }
    getGameToken(id: number) {
        this.oidcSecurityService.authorize('gameTokenConfig', {customParams: { 'game_id': id } })
    }
    publishChosenGame(game: GameplayModel) {
        game.config.username = UserInfo.currentUser?.username ?? 'unknown'
        if(GameInfo.accessToken === undefined){
            this.getGameToken(game.config.game_id)
            GameInfo.authStatus.subscribe(authStatus => {
                if(authStatus){
                    game.config.accessToken = GameInfo.accessToken
                    this.chosenGame.next(game)
                }
            });
        }else{
            game.config.accessToken = GameInfo.accessToken
            this.chosenGame.next(game)
        }
    }

}
