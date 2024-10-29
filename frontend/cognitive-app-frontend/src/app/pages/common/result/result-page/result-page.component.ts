import {Component, OnInit, ViewChild} from '@angular/core';
import {ResultService, SearchOptions} from "../../../../service/result/result.service";
import {GameManagementService} from "../../../../service/game-management/game-management.service";
import {map, Observable, of} from "rxjs";
import {Result} from "../../../../model/result.model";
import {Game} from "../../../../model/game.model";
import {MatPaginator, MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {PaginatorTranslator} from "../../../../common/paginator/paginator-translator";
import {SortElement} from "../../../../common/sort-control/sort-control.component";
import {AdminService} from "../../../../service/admin/admin.service";
import {TEXTS} from "../../../../utils/app.text_messages";
import {UserInfo} from "../../../../auth/userInfo";
import {AuthUser} from "../../../../model/user-contacts.model";
import {ActivatedRoute, Router} from "@angular/router";
import { Location } from '@angular/common';

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
    private chosenUsernames: string[] = [];
    private chosenGameIds: number[] = [];
    private chosenPassed: boolean | undefined = undefined;

    constructor(
        protected resultService: ResultService,
        private gameService: GameManagementService,
        private userService: AdminService,
        private router: Router,
        private route: ActivatedRoute,
        private location: Location,
    ) {
    }

    ngOnInit() {
        this.loadStateFromUrlParams();
        this.getResults();
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
        this.updateUrlParams();
    }

    onUserNamesChosen(users: AuthUser[] | undefined) {
        this.chosenUsernames = users?.map(u => u.username) ?? [];
        this.updateUrlParams();
    }

    onGamesChosen(games: Game[]) {
        this.chosenGameIds = games.filter(g => g.id).map(g => g.id!!) ?? [];
        this.updateUrlParams();
    }

    onPassedChosen(passed: string[] | undefined) {
        if (passed === undefined || passed.length != 1) {
            this.chosenPassed = undefined;
        } else {
            this.chosenPassed = passed[0] === this.text.result_info.passed;
        }
        this.updateUrlParams();
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
        this.getResults();
        this.updateUrlParams();
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
        if (this.chosenUsernames.length > 0) {
            options.usernames = this.chosenUsernames;
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

    private updateUrlParams(){
        const params = {
            pageIndex : this.lastPageEvent.pageIndex,
            pageSize : this.lastPageEvent.pageSize,
            sortBy: this.chosenSortElement.sortElement,
            sortOrder: this.chosenSortElement.sortDirection,
            chosenUserNames : this.chosenUsernames,
            chosenGameIds : this.chosenGameIds,
            chosenPassed : this.chosenPassed,
        }
        const urlTree = this.router.createUrlTree(['/result'], {
            relativeTo: this.route,
            queryParams: params,
            queryParamsHandling: 'merge',
        });
        this.location.go(urlTree.toString());
    }

    getSelectedUserName(){
        return this.route.snapshot.queryParamMap.get('chosenUserNames') ?? undefined;
    }
    getSelectedGameIds(){
        return (this.route.snapshot.queryParamMap.get('chosenGameIds')?.split(',') ?? []).map(Number);
    }

    private loadStateFromUrlParams(){
        this.lastPageEvent.pageIndex = (this.route.snapshot.queryParamMap.get('pageIndex') ?? 0) as number;
        this.lastPageEvent.pageSize = (this.route.snapshot.queryParamMap.get('pageSize') ?? this.defaultPageSize) as number;
        this.chosenSortElement = {
            sortElement: this.route.snapshot.queryParamMap.get('sortBy') ?? undefined,
            sortDirection: (this.route.snapshot.queryParamMap.get('sortOrder') ?? undefined) as 'ASC' | 'DESC' | undefined,
        }
        this.chosenUsernames = this.route.snapshot.queryParamMap.get('chosenUserNames')?.split(',') ?? [];
        this.chosenGameIds = (this.route.snapshot.queryParamMap.get('chosenGameIds')?.split(',') ?? []).map(Number);
        if(this.route.snapshot.queryParamMap.get('chosenPassed')) {
            this.chosenPassed = this.route.snapshot.queryParamMap.get('chosenPassed') === 'true';
        }
    }
}
