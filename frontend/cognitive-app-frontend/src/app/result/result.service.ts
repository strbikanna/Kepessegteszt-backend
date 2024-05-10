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
        if (searchOptions.gameNames) {
            params = params.set('gameNames', searchOptions.gameNames.join(','))
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

    getSortedAndPagedResults(results: Result[],sortBy: "timestamp" | "username" | "game", sortMode: "ASC" | "DESC", count: number) : Result[]{
        let sortedResults = results.sort((a, b) => {
            let order = 0;
                switch (sortBy) {
                    case "timestamp":
                        order = a.timestamp < b.timestamp ? -1 : 1; break;
                    case "username":
                        order = (a.username ?? 'a') < (b.username ?? 'b') ? -1 : 1; break;
                    case "game":
                        order = a.gameName < b.gameName ? -1 : 1; break;
                }
            return sortMode === "ASC" ? order : -order;
        });
        return sortedResults.slice(0, count);
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
    gameNames?: string[];
    passed?: boolean;
}
