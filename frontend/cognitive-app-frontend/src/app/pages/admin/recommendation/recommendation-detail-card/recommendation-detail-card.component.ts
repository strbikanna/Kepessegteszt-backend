import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {RecommendedGame} from "../../../../model/recommended_game.model";
import {TEXTS} from "../../../../text/app.text_messages";
import {MatDialog} from "@angular/material/dialog";
import {
  AddUserToGroupDialogComponent
} from "../../group-management/add-user-to-group-dialog/add-user-to-group-dialog.component";
import {User} from "../../../../model/user.model";
import {ConfirmDialogComponent} from "../../../../common/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'app-recommendation-detail-card',
  templateUrl: './recommendation-detail-card.component.html',
  styleUrls: ['./recommendation-detail-card.component.scss']
})
export class RecommendationDetailCardComponent {
  @Input({required: true}) recommendation!: RecommendedGame;
  @Output() deleteRecommendation: EventEmitter<number> = new EventEmitter<number>();

  text = TEXTS.recommendation_page.card

  dialog = inject(MatDialog);

  openConfirmDialog() {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.text.confirm_title,
        message: '',
        onOk: () => this.onRecommendationDelete(),
        onCancel: () => {
        }
      },
    });
  }

  getConfigKeys(): string[] {
    return Object.keys(this.recommendation.config);
  }

  private onRecommendationDelete() {
    this.deleteRecommendation.emit(this.recommendation.id!!);
  }

}
