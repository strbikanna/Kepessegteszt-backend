import { Component } from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {UserInfo} from "../auth/userInfo";
import {Observable} from "rxjs";
import {LoginService} from "../login/login.service";
import {User} from "../model/user.model";
import {RecommendedGameService} from "../game/game-services/recommended-game.service";
import {RecommendedGame} from "../model/recommended_game.model";
import {FormControl, FormGroup} from "@angular/forms";
import {Game} from "../model/game.model";

@Component({
  selector: 'app-recommendation',
  templateUrl: './recommendation.component.html',
  styleUrls: ['./recommendation.component.scss']
})
export class RecommendationComponent {
  user = UserInfo.currentUser
  contacts: Observable<User[]> = this.loginService.getContacts()
  text = TEXTS.recommendation_page;
  userToEdit: User | undefined = undefined;
  recommendedGameToEdit: RecommendedGame | undefined = undefined;
  notYetRecommendedGameToEdit: Game | undefined = undefined;
  games: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>()

  gamesToRecommend: Observable<Game[]> = new Observable<Game[]>()

  constructor(private loginService: LoginService, private recommendationService: RecommendedGameService) {
  }

  recommendedGameDataForm = new FormGroup({
    id: new FormControl<string>( ''),
    timestamp: new FormControl<any>(''),
    config: new FormControl<string>(''),
    completed: new FormControl<string>(''),
    game: new FormControl<any>(''),
  });

  notYetRecommendedGameDataForm = new FormGroup({
    config: new FormControl<string>(''),
    game: new FormControl<any>(''),
  });

  saveRecommendedGame(game: Game, recommendee: User) {
    this.recommendationService.saveRecommendedGame(recommendee, game, this.notYetRecommendedGameDataForm.getRawValue().config!)
    this.games = this.recommendationService.getRecommendedGamesToUser(recommendee)
    this.gamesToRecommend = this.recommendationService.getNotYetRecommendedGamesToUser(recommendee)
  }

  deleteRecommendedGame(recommendedGame: RecommendedGame, recommendee: User) {
    this.games.subscribe(games => games.forEach( (game, index) => {
      if (game === recommendedGame) {
        games.splice(index, 1)
      }
    }))
    this.recommendationService.deleteRecommendedGame(recommendedGame);
    this.games = this.recommendationService.getRecommendedGamesToUser(recommendee)
    this.gamesToRecommend = this.recommendationService.getNotYetRecommendedGamesToUser(recommendee)
  }

  setUserToEdit(user: User): void {
    this.userToEdit = user
    this.games = this.recommendationService.getRecommendedGamesToUser(user)
    this.gamesToRecommend = this.recommendationService.getNotYetRecommendedGamesToUser(user)
  }

  setRecommendedGameToEdit(rec_game: RecommendedGame) {
    this.recommendedGameToEdit = rec_game;
    this.recommendedGameDataForm.controls.id.setValue(rec_game.id)
    this.recommendedGameDataForm.controls.timestamp.setValue(rec_game.recommendationDate)
    this.recommendedGameDataForm.controls.config.setValue(rec_game.config.level)
    rec_game.completed ? this.recommendedGameDataForm.controls.completed.setValue('Teljesítve') : this.recommendedGameDataForm.controls.completed.setValue('Még nincs teljesítve')
    this.recommendedGameDataForm.controls.game.setValue(rec_game.game.name)
  }

  setNotYetRecommendedGameToEdit(game: Game) {
    this.notYetRecommendedGameToEdit = game
    this.notYetRecommendedGameDataForm.controls.game.setValue(game.name)
    this.notYetRecommendedGameDataForm.controls.config.setValue('0')
  }

}
