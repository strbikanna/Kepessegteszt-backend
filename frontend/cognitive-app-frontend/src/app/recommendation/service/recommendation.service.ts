import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
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
    console.log(recommendation)
    return this.http.post<RecommendedGame>(`${this.httpService.baseUrl}/recommended_game/recommend`, recommendation);
  }
}
