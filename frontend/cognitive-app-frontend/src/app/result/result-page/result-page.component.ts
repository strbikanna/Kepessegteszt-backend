import {Component, OnInit} from '@angular/core';
import {ResultService} from "../result.service";
import {GameManagementService} from "../../game-management/service/game-management.service";
import {Observable} from "rxjs";
import {Result} from "../../model/result.model";
import {Game} from "../../model/game.model";

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements  OnInit{

  protected results! : Observable<Result[]>;
  constructor(private resultService: ResultService, private gameService: GameManagementService) { }
  ngOnInit() {
    this.results = this.resultService.getAllResults()
  }

  getGame(gameId: number): Observable<Game> {
    return this.gameService.getGameById(gameId);
  }
}
