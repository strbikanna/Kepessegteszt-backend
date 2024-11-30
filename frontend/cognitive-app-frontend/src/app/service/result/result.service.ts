import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {catchError, map, Observable, retry} from "rxjs";
import {Result} from "../../model/result.model";
import {UserInfo} from "../../auth/userInfo";

@Injectable({
    providedIn: 'root'
})
export class ResultService {

    constructor(private http: HttpClient, private httpService: SimpleHttpService) {}

    csvPath = this.httpService.baseUrl + '/gameplay/csv';

    getAllResultsFiltered(searchOptions: SearchOptions = this.defaultSearchOptions): Observable<Result[]> {
        let params =  new HttpParams()
        params = params.set('sortBy', searchOptions.sortBy)
        params = params.set('sortOrder', searchOptions.sortOrder)
        params = params.set('pageIndex', searchOptions.pageIndex.toString())
        params = params.set('pageSize', searchOptions.pageSize.toString())
        if (searchOptions.gameIds) {
            params = params.set('gameIds', searchOptions.gameIds.join(','))
        }
        if (searchOptions.usernames && UserInfo.canSeeOthersResults()) {
            params = params.set('usernames', searchOptions.usernames.join(','))
        }
        if (searchOptions.passed != null) {
            params = params.set('resultWin', searchOptions.passed.toString())
        }
        let path = UserInfo.canSeeOthersResults() ? '/gameplay/results/all' : '/gameplay/results';

        return this.http.get<Result[]>(this.httpService.baseUrl + path, {params: params}).pipe(
            map((data: Result[]) =>
                data.map((result: Result) => this.mapResult(result))
            ),
            retry(3),
            catchError(this.httpService.handleHttpError)
        )
    }

    getCountOfResults(usernames?: string[], gameIds?: number[], passed?: boolean): Observable<number> {
        let params =  new HttpParams()
        if (gameIds) {
            params = params.set('gameIds', gameIds.join(','))
        }
        if (usernames && UserInfo.canSeeOthersResults()) {
            params = params.set('usernames', usernames.join(','))
        }
        if (passed != null) {
            params = params.set('resultWin', passed.toString())
        }
        return this.http.get<number>(this.httpService.baseUrl + '/gameplay/count', {params: params}).pipe(
            retry(3),
            catchError(this.httpService.handleHttpError)
        )
    }
    private mapResult(result: Result): Result {
        return {
            ...result,
            passed: result.result.passed
        }
    }
    private defaultSearchOptions: SearchOptions = {
            sortBy: "timestamp",
            sortOrder: "DESC",
            pageIndex: 0,
            pageSize: 10,
    }
}
export interface SearchOptions {
    sortBy: "timestamp" | "username" | "game";
    sortOrder: "ASC" | "DESC";
    pageIndex: number;
    pageSize: number;
    usernames?: string[];
    gameIds?: number[];
    passed?: boolean;
}
