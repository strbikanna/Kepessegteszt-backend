import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {RecommendedGame} from "../../model/recommended_game.model";
import {map, Observable} from "rxjs";
import {Recommendation} from "../../model/recommendation.model";
import SpecialSettings from "../../model/user/special_settings.model";

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
  deleteRecommendation(id: number){
    return this.http.delete(`${this.httpService.baseUrl}/recommended_game/${id}`);
  }

  getSpecialSettingsOfUser(username: string): Observable<SpecialSettings>{
    let params = new HttpParams().set('username', username);
    return this.http.get<SpecialSettings[]>(`${this.httpService.baseUrl}/special_settings`, {params: params}).pipe(
        map(response =>response[0])
    );
  }

  updateSpecialSettingsOfUser(username: string, settings: SpecialSettings): Observable<SpecialSettings>{
    let params = new HttpParams().set('username', username);
    return this.http.put<SpecialSettings[]>(`${this.httpService.baseUrl}/special_settings`, settings, {params: params}).pipe(
        map(response =>response[0])
    );
  }

}
