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



  private mockData: Observable<Game[]> = of([
    {
      id: 2,
      name: "Pop the balloons",
      description: "Try not to die from the bombs.",
      thumbnail: "../../assets/balloon_game.jpg",
      url: undefined,
      configDescription: {
        maxLevel: 5,
        maxPoints: 50,
        maxExtraPoints: 10,
        maxTime: 60,
        extraPointsName: "Health points",
        pointsName: "Score",
      },
      active: true,
      version: 1,
      affectedAbilities: [],
    },
    {
      id: 3,
      name: "Radio buttons",
      description: "Push the correct buttons of the secret radio.",
      thumbnail: "../../assets/number_game.jpg",
      url: undefined,
      configDescription: {
        maxLevel: 5,
        maxPoints: 100,
        maxTime: 60,
        extraPointsName: "Extra points",
        pointsName: "Points",
      },
      active: false,
      version: 2,
      affectedAbilities: [],
    },
    {
      id: 4,
      name: "Radio buttons",
      description: "Will you push the correct buttons of the secret radio?",
      thumbnail: "../../assets/number_game.jpg",
      url: undefined,
      configDescription: {
        maxLevel: 6,
        maxPoints: 100,
        maxTime: 60,
        extraPointsName: "Extra points",
        pointsName: "Points",
      },
      active: false,
      version: 1,
        affectedAbilities: [],
    },
    {
      id: 6,
      name: "Radio buttons",
      description: "Push the correct buttons of the secret radio! And here comes the very and mostest extra long text to test the wrapping. ",
      thumbnail: "../../assets/number_game.jpg",
      url: undefined,
      configDescription: {
        maxLevel: 8,
        maxPoints: 100,
        maxTime: 60,
        extraPointsName: "Extra points",
        pointsName: "Points",
      },
      active: true,
      version: 3,
        affectedAbilities: [],
    }
      ]
  )
}
