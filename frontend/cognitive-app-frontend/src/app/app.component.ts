import {Component} from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent{
  title = 'cognitive-app-frontend';
  drawerState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  drawerStateChanged(state: boolean){
    this.drawerState.next(state);
  }

}
