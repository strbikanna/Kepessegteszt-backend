import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {GameManagementService} from "../../../service/game-management/game-management.service";
import {Observable} from "rxjs";
import {Game} from "../../../model/game.model";
import {TEXTS} from "../../../text/app.text_messages";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {CalculationDialogComponent} from "./calculation-dialog/calculation-dialog.component";
import {Ability} from "../../../model/ability.model";
import {PaginatorTranslator} from "../../../common/paginator/paginator-translator";

@Component({
    selector: 'app-game-management',
    templateUrl: './game-management-page.component.html',
    styleUrls: ['./game-management-page.component.scss'],
    providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class GameManagementPageComponent implements OnInit {
    actionText = TEXTS.actions
    activeFilterText = TEXTS.game_management.active_filter
    dataLength = 100;
    pageSizeOptions = [10, 25, 50, 100];
    defaultPageSize = 50;
    lastPageEvent: PageEvent | undefined = undefined;
    games!: Observable<Game[]>
    activeFilter?: boolean | undefined = true

    @ViewChild('paginatorTop') paginatorTop!: MatPaginator
    @ViewChild('paginatorBottom') paginatorBottom!: MatPaginator

    constructor(
        private service: GameManagementService,
        private router: Router,
        private dialog: MatDialog,

    ) {
    }

    ngOnInit(): void {
        this.games = this.service.getExistingGamesPaged(0, this.defaultPageSize, this.activeFilter);
        this.service.getGamesCount().subscribe( count => this.dataLength = count)
    }

    handlePageEvent(event: PageEvent): void {
        this.paginatorTop.pageIndex = event.pageIndex;
        this.paginatorBottom.pageIndex = event.pageIndex;
        this.lastPageEvent = event;
        this.defaultPageSize = event.pageSize;
        this.games = this.service.getExistingGamesPaged(event.pageIndex, event.pageSize, this.activeFilter);
    }

    onActiveFilterSelected(){
        this.activeFilter = this.getActiveFilterNextValue();
        this.service.getGamesCount(this.activeFilter).subscribe( count => this.dataLength = count)
        this.games = this.service.getExistingGamesPaged(0, this.lastPageEvent?.pageSize ?? this.defaultPageSize, this.activeFilter);
    }

    navigateToEdit(game: Game) {
        this.router.navigate(['edit-game', game.id],);
    }

    navigateToNewGame() {
        this.router.navigate(['edit-game']);
    }

    getCardTexts(game: Game): string[] {
        let appTexts = TEXTS.game_management.edit_form
        let texts: string[] = [];
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
    private getActiveFilterNextValue(){
        if(this.activeFilter === undefined) return true;
        if(this.activeFilter) return false;
        return undefined;
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
