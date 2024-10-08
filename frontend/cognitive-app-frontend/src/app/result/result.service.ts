import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../utils/simple-http.service";
import {catchError, map, Observable, retry} from "rxjs";
import {Result} from "../model/result.model";
import {UserInfo} from "../auth/userInfo";

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
        if (searchOptions.usernames && UserInfo.isAdmin()) {
            params = params.set('usernames', searchOptions.usernames.join(','))
        }
        if (searchOptions.passed != null) {
            params = params.set('resultWin', searchOptions.passed.toString())
        }
        let path = UserInfo.isAdmin() ? '/gameplay/results/all' : '/gameplay/results';

        return this.http.get<Result[]>(this.httpService.baseUrl + path, {params: params}).pipe(
            map((data: Result[]) =>
                data.map((result: Result) => this.mapResult(result))
            ),
            retry(3),
            catchError(this.httpService.handleHttpError)
        )
    }

    getCountOfResults(): Observable<number> {
        let path = UserInfo.isAdmin() ? '/gameplay/count/all' : '/gameplay/count'
        return this.http.get<number>(this.httpService.baseUrl + path).pipe(
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
