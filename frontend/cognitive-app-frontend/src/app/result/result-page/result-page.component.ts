import {Component, OnInit} from '@angular/core';
import {ResultService, SearchOptions} from "../result.service";
import {GameManagementService} from "../../game-management/service/game-management.service";
import {map, Observable, of, tap} from "rxjs";
import {Result} from "../../model/result.model";
import {Game} from "../../model/game.model";
import {MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {PaginatorTranslator} from "../../common/paginator/paginator-translator";
import {SortElement} from "../../common/sort-control/sort-control.component";
import {UserService} from "angular-auth-oidc-client/lib/user-data/user.service";
import {AdminService} from "../../admin/service/admin.service";
import {TEXTS} from "../../utils/app.text_messages";

@Component({
    selector: 'app-result-page',
    templateUrl: './result-page.component.html',
    styleUrls: ['./result-page.component.scss'],
    providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class ResultPageComponent implements OnInit {

    protected results!: Observable<Result[]>;
    protected text = TEXTS.result

    pageSizeOptions = [5, 25, 100];
    dataLength = 0;
    defaultPageSize = 10;
    lastPageEvent: PageEvent = {pageIndex: 0, pageSize: this.defaultPageSize, length: this.dataLength};
    userNameOptions: Observable<string[]> = new Observable<string[]>();
    gameNameOptions: Observable<string[]> = new Observable<string[]>();
    sortOptions: string[] = [this.text.timestamp, this.text.username, this.text.game];

    private chosenSortElement: SortElement = {sortElement: undefined, sortDirection: undefined};
    private chosenUserNames: string[] = [];
    private chosenGameNames: string[] = [];
    private chosenPassed: boolean | undefined = undefined;

    constructor(protected resultService: ResultService, private gameService: GameManagementService, private userService: AdminService) {
    }

    ngOnInit() {
        this.results = this.resultService.getAllResultsFiltered()
        this.resultService.getCountOfResults().subscribe(count => this.dataLength = count);
        this.loadGameNames();
        this.loadUsernames();
    }

    onSortElementChosen(sortElement: SortElement) {
        this.chosenSortElement = sortElement;
    }

    onUserNamesChosen(userNames: string[] | undefined) {
        this.chosenUserNames = userNames ?? [];
    }

    onGameNamesChosen(gameNames: string[] | undefined) {
        this.chosenGameNames = gameNames ?? [];
    }

    onPassedChosen(passed: string[] | undefined) {
        console.log('onPassedChosen'
            , passed)
        if (passed === undefined || passed.length != 1) {
            this.chosenPassed = undefined;
        } else {
            this.chosenPassed = passed[0] === this.text.result_info.passed;
        }
        console.log('this.chosenPassed', this.chosenPassed)
    }

    getResults() {
        this.results = this.resultService.getAllResultsFiltered(this.getSearchOptions());
    }

    handlePageEvent(event: PageEvent): void {
        this.defaultPageSize = event.pageSize;
        this.lastPageEvent = event;
        this.getResults()
    }

    private loadGameNames() {
        console.log('loadGameNames')
        this.gameNameOptions = this.gameService.getExistingGamesPaged().pipe(
            map((games: Game[]) => games.map(game => game.name))
        )
    }

    private loadUsernames() {
        this.userNameOptions = this.userService.getAllUsers().pipe(
            map(users => users.map(user => user.username))
        )
    }

    private getSearchOptions(): SearchOptions {
        let options: SearchOptions = {
            pageIndex: this.lastPageEvent.pageIndex,
            pageSize: this.lastPageEvent.pageSize,
            sortBy: this.getSortBy(this.chosenSortElement.sortElement) ?? 'timestamp',
            sortOrder: this.chosenSortElement.sortDirection ?? 'DESC',
        }
        if (this.chosenGameNames.length > 0) {
            options.gameNames = this.chosenGameNames;
        }
        if (this.chosenUserNames.length > 0) {
            options.usernames = this.chosenUserNames;
        }
        if(this.chosenPassed !== undefined) {
            options.passed = this.chosenPassed;
        }
        return options;
    }

    private getSortBy(sortElement: string | undefined): 'timestamp' | 'username' | 'game' {
        switch (sortElement) {
            case this.text.timestamp:
                return 'timestamp';
            case this.text.username:
                return 'username';
            case this.text.game:
                return 'game';
            default:
                return 'timestamp';
        }
    }

    protected readonly of = of;
}
