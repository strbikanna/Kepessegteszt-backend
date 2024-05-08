import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
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

    getAllResults(pageIndex: number = 0, pageSize: number = 10,
                  sortBy: "timestamp" | "user" | "username" | "game" = "timestamp",
                  sortOrder: "ASC" | "DESC" = "DESC"): Observable<Result[]> {
        const params = {
            pageIndex: pageIndex.toString(),
            pageSize: pageSize.toString(),
            sortBy: sortBy,
            sortOrder: sortOrder
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
}
