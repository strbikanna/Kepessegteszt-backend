import {Component, Inject, OnInit,} from '@angular/core';
import {TEXTS} from "../../../../utils/app.text_messages";
import {GameManagementService} from "../../../../service/game-management/game-management.service";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {Game} from "../../../../model/game.model";
import {CalculationFeedback} from "../../../../model/calculation-feedback.model";
import {Ability} from "../../../../model/ability.model";

/**
 * Dialog that shows game result data calculation data and actions.
 */
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
    resultCount: number = -1;

    constructor(
        private service: GameManagementService,
        @Inject(MAT_DIALOG_DATA) private gameId: number
    ) {}

    /**
     * Queries game data and current count of result data to game based on injected id.
     */
    ngOnInit(): void {
        this.service.getGameById(this.gameId).subscribe(game =>
            this.game = game
        )
        this.getResultCount(this.gameId).subscribe(
            resultCount => this.resultCount = resultCount
        );

    }

    /**
     * Calls backend to start result processing for game.
     * @param game
     */
    onStartCalculation(game: Game) {
        this.inProgress = true;
        this.service.startResultProcessing(game.id!!).subscribe(result => {
            this.inProgress = false;
            this.feedback = result;
        })
    }

    canStartCalculation(){
        return this.game !== undefined && !this.inProgress && this.feedback === undefined && this.resultCount && this.resultCount > 0
            && this.game.affectedAbilities && this.game.affectedAbilities.length > 0;
    }

    getResultCount(gameId: number): Observable<number> {
        return this.service.getResultCountOfGame(gameId);
    }

    transformAffectedAbilities(abilities: Ability[]): string {
        if(!abilities) return TEXTS.game_management.calculation_dialog.no_abilities;
        let abilitiesNames: string[] = abilities.map(ability => ability.name)
        return abilitiesNames.join(', ');
    }
}
