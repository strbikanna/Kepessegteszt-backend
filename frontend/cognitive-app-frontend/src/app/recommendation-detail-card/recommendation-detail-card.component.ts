import {Component, Input} from '@angular/core';
import {RecommendedGame} from "../model/recommended_game.model";

@Component({
  selector: 'app-recommendation-detail-card',
  templateUrl: './recommendation-detail-card.component.html',
  styleUrls: ['./recommendation-detail-card.component.scss']
})
export class RecommendationDetailCardComponent {
  @Input({required: true}) recommendation!: RecommendedGame;

  getConfigKeys(): string[] {
    return Object.keys(this.recommendation.config);
  }

}
