import {Component, OnInit} from '@angular/core';
import {PageEvent} from "@angular/material/paginator";
import {GameManagementService} from "./service/game-management.service";
import {Observable} from "rxjs";
import {Game} from "../model/game.model";
import {TEXTS} from "../utils/app.text_messages";
import {Router} from "@angular/router";
import {CalculationFeedback} from "../model/calculation-feedback.model";
import {AlertDialogComponent} from "../common/alert-dialog/alert-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {CalculationDialogComponent} from "./calculation-dialog/calculation-dialog.component";

@Component({
  selector: 'app-game-management',
  templateUrl: './game-management.component.html',
  styleUrls: ['./game-management.component.scss']
})
export class GameManagementComponent implements OnInit{
  actionText = TEXTS.actions
  dataLength = 100;
  pageSizeOptions = [10, 25, 100];
  defaultPageSize = 10;
  lastPageEvent: PageEvent | undefined = undefined;
  games!: Observable<Game[]>


  constructor(private service: GameManagementService, private router: Router, private dialog: MatDialog) {}
  ngOnInit(): void {
    this.games = this.service.getExistingGamesPaged(0, this.defaultPageSize);
  }
  handlePageEvent(event: PageEvent): void {
    this.lastPageEvent = event;
    this.defaultPageSize = event.pageSize;
    this.games = this.service.getExistingGamesPaged(event.pageIndex, event.pageSize);
  }
  navigateToEdit(game: Game){
    this.router.navigate(['edit-game', game.id], {relativeTo: this.router.routerState.root});
  }
  getResultCount(game: Game): Observable<number>{
    return this.service.getResultCountOfGame(game);
  }

  startResultProcessing(game: Game){
        this.dialog.open(
            CalculationDialogComponent,
            {
              data: {gameId: game.id},
              disableClose: true,
            }
        )
  }

}
