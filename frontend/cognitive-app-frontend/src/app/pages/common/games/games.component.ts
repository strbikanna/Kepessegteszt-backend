import { Component } from '@angular/core';
import {TEXTS} from "../../../text/app.text_messages";
import {GameManagementPageComponent} from "../../admin/game-management/game-management-page.component";


@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrl: './games.component.scss'
})
export class GamesComponent extends GameManagementPageComponent {
 text = TEXTS.games;


}
