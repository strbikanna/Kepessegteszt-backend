import {Component, Inject, OnInit,} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {GameManagementService} from "../service/game-management.service";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {Game} from "../../model/game.model";
import {CalculationFeedback} from "../../model/calculation-feedback.model";
import {Ability} from "../../model/ability.model";

@Component({
    selector: 'app-calculation-dialog',
    templateUrl: './calculation-dialog.component.html',
    styleUrls: ['./calculation-dialog.component.scss']
})
export class CalculationDialogComponent implements OnInit {
    text = TEXTS.game_management.calculation_dialog
    cancelButtonText = TEXTS.actions.cancel
    okButtonText = TEXTS.actions.ok
    confirmButtonText = TEXTS.actions.calculation
    game: Game | undefined
    inProgress = false;
    feedback: CalculationFeedback | undefined;

    constructor(
        private service: GameManagementService,
        @Inject(MAT_DIALOG_DATA) private gameId: number
    ) {
    }

    ngOnInit(): void {
        this.service.getGameById(this.gameId).subscribe(game =>
            this.game = game
        )
    }

    onStartCalculation(game: Game) {
        this.inProgress = true;
        this.service.startResultProcessing(game).subscribe(result => {
            this.inProgress = false;
            this.feedback = result;
        })
    }

    canStartCalculation(){
        return this.game !== undefined && !this.inProgress && this.feedback === undefined;
    }

    resultCount(game: Game): Observable<number> {
        return this.service.getResultCountOfGame(game);
    }

    transformAffectedAbilities(abilities: Ability[]): string {
        let abilitiesNames: string[] = abilities.map(ability => ability.name)
        return abilitiesNames.join(', ');
    }
}
