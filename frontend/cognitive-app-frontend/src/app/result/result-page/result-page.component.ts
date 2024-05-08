import {Component, OnInit} from '@angular/core';
import {ResultService} from "../result.service";
import {GameManagementService} from "../../game-management/service/game-management.service";
import {Observable, of} from "rxjs";
import {Result} from "../../model/result.model";
import {Game} from "../../model/game.model";
import {MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {PaginatorTranslator} from "../../common/paginator/paginator-translator";

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss'],
  providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class ResultPageComponent implements  OnInit{

  protected results! : Observable<Result[]>;

  pageSizeOptions = [5, 25, 100];
  dataLength = 0;
  defaultPageSize = 10;
  constructor(protected resultService: ResultService, private gameService: GameManagementService) { }
  ngOnInit() {
    this.results = this.resultService.getAllResults()
    this.resultService.getCountOfResults().subscribe(count => this.dataLength = count);
  }


  getGame(gameId: number): Observable<Game> {
    return this.gameService.getGameById(gameId);
  }

  handlePageEvent(event: PageEvent): void {
    this.defaultPageSize = event.pageSize;
    this.results = this.resultService.getAllResults(event.pageIndex, event.pageSize);
  }
}
