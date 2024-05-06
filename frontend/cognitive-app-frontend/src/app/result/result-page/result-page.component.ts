import {Component, OnInit} from '@angular/core';
import {ResultService} from "../result.service";
import {GameManagementService} from "../../game-management/service/game-management.service";
import {Observable} from "rxjs";
import {Result} from "../../model/result.model";
import {Game} from "../../model/game.model";
import {PageEvent} from "@angular/material/paginator";

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements  OnInit{

  protected results! : Observable<Result[]>;

  pageSizeOptions = [10, 25, 100];
  dataLength = 0;
  defaultPageSize = 10;
  constructor(private resultService: ResultService, private gameService: GameManagementService) { }
  ngOnInit() {
    this.results = this.resultService.getAllResults()
  }

  getGame(gameId: number): Observable<Game> {
    return this.gameService.getGameById(gameId);
  }

  handlePageEvent(event: PageEvent): void {
    this.defaultPageSize = event.pageSize;
    this.results = this.resultService.getAllResults(event.pageIndex, event.pageSize);
  }
}
