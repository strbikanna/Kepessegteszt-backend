import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UserInfo} from "../auth/userInfo";
import {Role} from "../utils/constants";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    loginStatus = false
    isAdmin = false;

    constructor(private changeDetectorRef: ChangeDetectorRef) {
    }

    ngOnInit(): void {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            this.loginStatus = loginSuccess
            this.isAdmin = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.ADMIN) !== undefined && loginSuccess
            this.changeDetectorRef.detectChanges()
        });
    }

}
