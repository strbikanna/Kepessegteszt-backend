import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {Game} from "../model/game.model";
import {CalculationFeedback} from "../model/calculation-feedback.model";

@Injectable({
  providedIn: 'root'
})
export class GameManagementService {

  constructor(http: HttpClient) { }

  getExistingGamesPaged(pageIndex: number, pageSize: number): Observable<Game[]> {
    return this.mockData;
  }

  getResultCountOfGame(game: Game): Observable<number> {
    return of(200);
  }

  startResultProcessing(game: Game): Observable<CalculationFeedback> {
    return of({
      mean: 0.7,
      deviation: 0.1,
      updatedProfilesCount: 100,
    })
  }

  private mockData: Observable<Game[]> = of([
    {
      id: 2,
      name: "Pop the balloons",
      description: "Try not to die from the bombs.",
      thumbnail: "../../assets/balloon_game.jpg",
      url: undefined,
      config: {
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
      config: {
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
      config: {
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
      config: {
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
