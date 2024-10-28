import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {RecommendedGame} from "../../model/recommended_game.model";
import {Observable} from "rxjs";
import {Recommendation} from "../../model/recommendation.model";

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {

  constructor(private http: HttpClient, private httpService: SimpleHttpService) { }

  saveRecommendation(recommendation: Recommendation): Observable<RecommendedGame>{
    return this.http.post<RecommendedGame>(`${this.httpService.baseUrl}/recommended_game/recommend`, recommendation);
  }
  getRecommendationsToUserAndGame(username: string, gameId?: number): Observable<RecommendedGame[]>{
    let params = new HttpParams().set('username', username);
    if(gameId){
      params = params.set('gameId', gameId.toString());
    }
    return this.http.get<RecommendedGame[]>(`${this.httpService.baseUrl}/recommended_game/search`, {params: params});
  }
}
