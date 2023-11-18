import { Injectable } from '@angular/core';
import {delay, Observable, of} from "rxjs";
import {Game} from "../../model/game.model";
import {CalculationFeedback} from "../../model/calculation-feedback.model";
import {SimpleHttpService} from "../../utils/simple-http.service";

@Injectable({
  providedIn: 'root'
})
export class GameManagementService extends SimpleHttpService{


  getExistingGamesPaged(pageIndex: number, pageSize: number): Observable<Game[]> {
    return this.mockData;
  }

  getGameById(id: number): Observable<Game> {
    return of(
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
        }
    )
  }

  editGame(game: Game): Observable<Game> {
    return of(
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
    ).pipe(delay(1000));
  }
  sendGameThumbnail(thumbnail: FormData, gameId: number){
    return of(200);
  }

  getResultCountOfGame(game: Game): Observable<number> {
    return of(200);
  }

  startResultProcessing(game: Game): Observable<CalculationFeedback> {
    delay(1000)
    return of({
      mean: 0.7,
      deviation: 0.1,
      updatedProfilesCount: 100,
    }).pipe(delay(5000))
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
