import {Component, Input,} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {Observable, of} from "rxjs";
import {UserInfo} from "../../auth/userInfo";

@Component({
    selector: 'app-side-menu',
    templateUrl: './side-menu.component.html',
    styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent extends HeaderComponent {

    @Input() isDrawerOpenObservable: Observable<boolean> = of(true);

    protected readonly UserInfo = UserInfo;
}
