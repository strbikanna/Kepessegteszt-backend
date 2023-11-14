import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Game} from "../../model/game.model";
import {Ability} from "../../model/ability.model";

@Component({
  selector: 'app-game-card',
  templateUrl: './game-card.component.html',
  styleUrls: ['./game-card.component.scss']
})
export class GameCardComponent {
  text = TEXTS.games;

  @Input() buttonText: string | undefined;
  @Input() extraButtonText: string | undefined;
  @Input({required: true}) game!: Game
  @Output() buttonClick: EventEmitter<Game> = new EventEmitter<Game>();
  @Output() extraButtonClick: EventEmitter<Game> = new EventEmitter<Game>();

  raiseClickEvent(game: Game){
    this.buttonClick.emit(game);
  }
  raiseExtraClickEvent(game: Game){
    this.extraButtonClick.emit(game);
  }
  transformAffectedAbilities(abilities: Ability[]): string{
    let abilitiesNames: string[] = abilities.map(ability => ability.name)
    return abilitiesNames.join(', ');
  }
}
