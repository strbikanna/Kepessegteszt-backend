import {Component, OnInit, ViewChild} from '@angular/core';
import {ResultService, SearchOptions} from "../../../../service/result/result.service";
import {GameManagementService} from "../../../../service/game-management/game-management.service";
import {map, Observable, of, tap} from "rxjs";
import {Result} from "../../../../model/result.model";
import {Game} from "../../../../model/game.model";
import {MatPaginator, MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {PaginatorTranslator} from "../../../../common/paginator/paginator-translator";
import {SortElement} from "../../../../common/sort-control/sort-control.component";
import {UserService} from "angular-auth-oidc-client/lib/user-data/user.service";
import {AdminService} from "../../../../service/admin/admin.service";
import {TEXTS} from "../../../../utils/app.text_messages";
import {UserInfo} from "../../../../auth/userInfo";
import {AuthUser} from "../../../../model/user-contacts.model";

@Component({
    selector: 'app-result-page',
    templateUrl: './result-page.component.html',
    styleUrls: ['./result-page.component.scss'],
    providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class ResultPageComponent implements OnInit {

    protected results!: Result[];
    protected text = TEXTS.result

    pageSizeOptions = [5, 25, 50];
    dataLength = 0;
    defaultPageSize = 5;
    lastPageEvent: PageEvent = {pageIndex: 0, pageSize: this.defaultPageSize, length: this.dataLength};
    userNameOptions: Observable<string[]> = new Observable<string[]>();
    gameNameOptions: Observable<string[]> = new Observable<string[]>();
    sortOptions: string[] = [this.text.timestamp, this.text.game];
    loading = true;

    @ViewChild('paginator') paginator!: MatPaginator

    private chosenSortElement: SortElement = {sortElement: undefined, sortDirection: undefined};
    private chosenUserNames: string[] = [];
    private chosenGameIds: number[] = [];
    private chosenPassed: boolean | undefined = undefined;

    constructor(protected resultService: ResultService, private gameService: GameManagementService, private userService: AdminService) {
    }

    ngOnInit() {
        this.resultService.getAllResultsFiltered({
            pageIndex: 0,
            pageSize: this.defaultPageSize,
            sortBy: 'timestamp',
            sortOrder: 'DESC'
        }).subscribe(results => {
            this.results = results;
            this.loading = false;
        })
        this.resultService.getCountOfResults().subscribe(count => this.dataLength = count);
        this.loadGameNames();
        if (UserInfo.isAdmin()) {
            this.sortOptions.push(this.text.username)
            this.loadUsernames();
        }
    }

    onSortElementChosen(sortElement: SortElement) {
        this.chosenSortElement = sortElement;
        this.onApplyFilters();
    }

    onUserNamesChosen(users: AuthUser[] | undefined) {
        this.chosenUserNames = users?.map(u => u.username) ?? [];
    }

    onGamesChosen(games: Game[]) {
        this.chosenGameIds = games.filter(g => g.id).map(g => g.id!!) ?? []
    }

    onPassedChosen(passed: string[] | undefined) {
        if (passed === undefined || passed.length != 1) {
            this.chosenPassed = undefined;
        } else {
            this.chosenPassed = passed[0] === this.text.result_info.passed;
        }
    }

    onApplyFilters() {
        this.paginator.firstPage();
        this.loading = true;
        this.resultService.getCountOfResults().subscribe(count => this.dataLength = count);
        this.getResults();
    }

    getResults() {
        this.loading = true;
        this.resultService.getAllResultsFiltered(this.getSearchOptions())
            .subscribe(results => {
                    this.results = results;
                    this.loading = false;
                }
            )
    }

    handlePageEvent(event: PageEvent): void {
        this.lastPageEvent = event;
        this.getResults()
    }

    isAdmin(): boolean {
        return UserInfo.isAdmin();
    }

    passedFilterElements(): Observable<string[]>{
        return of([this.text.result_info.passed, this.text.result_info.failed])
    }

    private loadGameNames() {
        this.gameNameOptions = this.gameService.getExistingGamesPaged().pipe(
            map((games: Game[]) => games.map(game => `${game.name} (${game.id})`)),
        )
    }

    private loadUsernames() {
        this.userNameOptions = this.userService.getAllUsers().pipe(
            map(users => users.map(user => `${user.firstName} ${user.lastName} (${user.username})`))
        )
    }

    private getSearchOptions(): SearchOptions {
        let options: SearchOptions = {
            pageIndex: this.lastPageEvent.pageIndex,
            pageSize: this.lastPageEvent.pageSize,
            sortBy: this.getSortBy(this.chosenSortElement.sortElement) ?? 'timestamp',
            sortOrder: this.chosenSortElement.sortDirection ?? 'DESC',
        }
        if (this.chosenGameIds.length > 0) {
            options.gameIds = this.chosenGameIds;
        }
        if (this.chosenUserNames.length > 0) {
            options.usernames = this.chosenUserNames;
        }
        if (this.chosenPassed !== undefined) {
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
}
