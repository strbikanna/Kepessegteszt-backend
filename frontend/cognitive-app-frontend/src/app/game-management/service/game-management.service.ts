import { Injectable } from '@angular/core';
import {catchError, map, Observable, retry} from "rxjs";
import {Game} from "../../model/game.model";
import {CalculationFeedback} from "../../model/calculation-feedback.model";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {HttpClient, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class GameManagementService {
  private path = "/game";
  private calcPath = "/profile-calculation";

  constructor(private helper: SimpleHttpService, private http: HttpClient) { }
  getExistingGamesPaged(pageIndex?: number, pageSize?: number): Observable<Game[]> {
    let params = new HttpParams()
    if(pageIndex) params = params.set('pageIndex', pageIndex.toString())
    if(pageSize) params = params.set('pageSize', pageSize.toString())
    return this.http.get<Game[]>(`${this.helper.baseUrl}${this.path}/all`, {params: params}).pipe(
        retry(3),
        catchError(this.helper.handleHttpError)
    )
  }
  getGamesCount(): Observable<number> {
    return this.http.get<number>(`${this.helper.baseUrl}${this.path}/all/count`).pipe(
        retry(3),
        catchError(this.helper.handleHttpError)
    )
  }

  getGameById(id: number): Observable<Game> {
    return this.http.get<Game>(`${this.helper.baseUrl}${this.path}/${id}`).pipe(
        retry(3),
        catchError(this.helper.handleHttpError)
    )
  }

  editGame(game: Game): Observable<Game> {
    if(!game.id) return this.createGame(game);

    return this.http.put<Game>(`${this.helper.baseUrl}${this.path}/${game.id}`, game).pipe(
        retry(3),
    )
  }

  createGame(game: Game): Observable<Game> {
    return this.http.post<Game>(`${this.helper.baseUrl}${this.path}`, game).pipe(
        retry(3),
    )
  }
  sendGameThumbnail(thumbnail: FormData, gameId: number): Observable<Game>{
    return this.http.post<Game>(`${this.helper.baseUrl}${this.path}/image/${gameId}`, thumbnail).pipe(
        retry(3),
    )
  }

  /**
   * Returns the number of unprocessed game play results for a given game
   * @param gameId
   */
  getResultCountOfGame(gameId: number): Observable<number> {
    let params = new HttpParams()
        .set('gameId', gameId.toString());
    return this.http.get<number>(`${this.helper.baseUrl}${this.calcPath}/result_count`, {params: params}).pipe(
        retry(3),
        catchError(this.helper.handleHttpError)
    )
  }

  startResultProcessing(gameId: number): Observable<CalculationFeedback> {
    let params = new HttpParams()
        .set('gameId', gameId.toString());
    return this.http.post<CalculationFeedback>(`${this.helper.baseUrl}${this.calcPath}/process_results`, {},{params: params}).pipe(
        retry(3),
        catchError(this.helper.handleHttpError),
        map((response: any) => {
            return {
                mean: response.meanAndDeviation.mean,
                deviation: response.meanAndDeviation.deviation,
                updatedProfilesCount: response.updatedProfilesCount
            }
        })
    )
  }
}
