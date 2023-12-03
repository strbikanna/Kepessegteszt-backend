import { Injectable } from '@angular/core';
import {delay, Observable, of} from "rxjs";
import {Game} from "../../model/game.model";
import {CalculationFeedback} from "../../model/calculation-feedback.model";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class GameManagementService extends SimpleHttpService{
  private path = "/game";
  private calcPath = "/profile-calculation";
  getExistingGamesPaged(pageIndex: number, pageSize: number): Observable<Game[]> {
    let params = new HttpParams()
        .set('page', pageIndex.toString())
        .set('size', pageSize.toString());
    return this.http.get<Game[]>(`${this.baseUrl}${this.path}/all`, {params: params})
  }
  getGamesCount(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}${this.path}/all/count`)
  }

  getGameById(id: number): Observable<Game> {
    return this.http.get<Game>(`${this.baseUrl}${this.path}/${id}`)
  }

  editGame(game: Game): Observable<Game> {
    return this.http.put<Game>(`${this.baseUrl}${this.path}/${game.id}`, game)
  }
  sendGameThumbnail(thumbnail: FormData, gameId: number): Observable<Game>{
    return this.http.post<Game>(`${this.baseUrl}${this.path}/image/${gameId}`, thumbnail)
  }

  getResultCountOfGame(gameId: number): Observable<number> {
    let params = new HttpParams()
        .set('gameId', gameId.toString());
    return this.http.get<number>(`${this.baseUrl}${this.calcPath}/result_count`, {params: params})
  }

  startResultProcessing(gameId: number): Observable<CalculationFeedback> {
    let params = new HttpParams()
        .set('gameId', gameId.toString());
    return this.http.post<CalculationFeedback>(`${this.baseUrl}${this.calcPath}/process_results`, {},{params: params})
  }


}
