import {Component, Input,} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {Observable, of} from "rxjs";

@Component({
    selector: 'app-side-menu',
    templateUrl: './side-menu.component.html',
    styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent extends HeaderComponent {

    @Input() isDrawerOpenObservable: Observable<boolean> = of(true);

}
