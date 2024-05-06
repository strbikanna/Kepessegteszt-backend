import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SimpleHttpService} from "../utils/simple-http.service";
import {catchError, map, Observable, retry} from "rxjs";
import {Result} from "../model/result.model";
import {UserInfo} from "../auth/userInfo";

@Injectable({
  providedIn: 'root'
})
export class ResultService {

  constructor(private http: HttpClient, private httpService: SimpleHttpService) { }

  getAllResults() : Observable<Result[]> {
      if (UserInfo.isAdmin()) {
          return this.http.get<Result[]>(this.httpService.baseUrl + '/gameplay/results/all').pipe(
              map((data: Result[]) =>
                  data.map((result: Result) => this.mapResult(result))
              ),
              retry(3),
              catchError(this.httpService.handleHttpError)
          )
      }
  else{
        return this.http.get<Result[]>(this.httpService.baseUrl + '/gameplay/results').pipe(
            map((data: Result[]) =>
                data.map((result: Result) => this.mapResult(result))
            ),
            retry(3),
            catchError(this.httpService.handleHttpError)
        )
    }

}

private mapResult(result: Result): Result {
    return {
        ...result,
        passed: result.result.passed
    }
}
}
