import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Game} from "../../model/game.model";

/**
 * Game card with uniform look.
 */
@Component({
  selector: 'app-game-card',
  templateUrl: './game-card.component.html',
  styleUrls: ['./game-card.component.scss']
})
export class GameCardComponent {
  text = TEXTS.games;

  /**
   * @param buttonText Text for the primary button.
   */
  @Input() buttonText: string | undefined;
  /**
   * @param extraButtonText Text for the secondary button.
   */
  @Input() extraButtonText: string | undefined;
  /**
   * @param game Game to be displayed.
   */
  @Input({required: true}) game!: Game
  /**
   * @param otherTexts Other texts to be displayed after game data in basic text style.
   */
  @Input() otherTexts: string[] | undefined;
  /**
   * @param buttonClick Event raised when the primary button is clicked.
   */
  @Output() buttonClick: EventEmitter<Game> = new EventEmitter<Game>();
  /**
   * @param extraButtonClick Event raised when the secondary button is clicked.
   */
  @Output() extraButtonClick: EventEmitter<Game> = new EventEmitter<Game>();

  raiseClickEvent(game: Game){
    this.buttonClick.emit(game);
  }
  raiseExtraClickEvent(game: Game){
    this.extraButtonClick.emit(game);
  }

}
