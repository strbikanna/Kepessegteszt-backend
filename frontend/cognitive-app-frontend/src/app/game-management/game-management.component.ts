import {Component, OnInit} from '@angular/core';
import {MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {GameManagementService} from "./service/game-management.service";
import {Observable} from "rxjs";
import {Game} from "../model/game.model";
import {TEXTS} from "../utils/app.text_messages";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {CalculationDialogComponent} from "./calculation-dialog/calculation-dialog.component";
import {Ability} from "../model/ability.model";
import {PaginatorTranslator} from "../common/paginator/paginator-translator";

@Component({
    selector: 'app-game-management',
    templateUrl: './game-management.component.html',
    styleUrls: ['./game-management.component.scss'],
    providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class GameManagementComponent implements OnInit {
    actionText = TEXTS.actions
    dataLength = 100;
    pageSizeOptions = [10, 25, 100];
    defaultPageSize = 10;
    lastPageEvent: PageEvent | undefined = undefined;
    games!: Observable<Game[]>


    constructor(
        private service: GameManagementService,
        private router: Router,
        private dialog: MatDialog
    ) {
    }

    ngOnInit(): void {
        this.games = this.service.getExistingGamesPaged(0, this.defaultPageSize);
        this.service.getGamesCount().subscribe( count => this.dataLength = count)
    }

    handlePageEvent(event: PageEvent): void {
        this.lastPageEvent = event;
        this.defaultPageSize = event.pageSize;
        this.games = this.service.getExistingGamesPaged(event.pageIndex, event.pageSize);
    }

    navigateToEdit(game: Game) {
        this.router.navigate(['edit-game', game.id],);
    }

    getCardTexts(game: Game): string[] {
        let appTexts = TEXTS.game_management.edit_form
        let texts: string[] = [];
        texts.push(appTexts.version + game.version);
        game.active ? texts.push(appTexts.active) : texts.push(appTexts.non_active);
        texts.push(`${appTexts.affected_abilities}: ${this.transformAffectedAbilities(game.affectedAbilities)}`);
        return texts;
    }
    private transformAffectedAbilities(abilities: Ability[]): string{
        if(abilities) {
            let abilitiesNames: string[] = abilities.map(ability => ability.name)
            return abilitiesNames.join(', ');
        }
        else return '';
    }

    /**
     * Opens a dialog to show the calculation progress
     * @param game
     */
    startResultProcessing(game: Game) {
        this.dialog.open(
            CalculationDialogComponent,
            {
                data: game.id,
                disableClose: true,
            }
        )
    }

}
