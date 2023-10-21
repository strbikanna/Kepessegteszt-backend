import {Component, OnInit} from '@angular/core';
import {UserInfo} from "./utils/userInfo";
import {Role} from "./utils/constants";
import {Observable, of} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent{
  title = 'cognitive-app-frontend';

}
