import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {UserInfo} from "../auth/userInfo";
import {Role} from "../utils/constants";
import {TEXTS} from "../utils/app.text_messages";
import {ActivatedRoute} from "@angular/router";


@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})

export class HeaderComponent implements OnInit{
    loginStatus = false;
    isAdmin = false;
    isAdminOrScientist = false;
    text = TEXTS.menu;
    isTeacher = false;
    isScientist = false;

    constructor(private changeDetectorRef: ChangeDetectorRef,) {  }

    ngOnInit(): void {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            this.loginStatus = loginSuccess
            this.isAdmin = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.ADMIN) !== undefined && loginSuccess
            this.isAdminOrScientist = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.ADMIN || role.toUpperCase() === Role.SCIENTIST) !== undefined && loginSuccess
            this.isTeacher = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.TEACHER) !== undefined  && this.loginStatus
            this.isScientist = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.SCIENTIST) !== undefined  && this.loginStatus
            this.changeDetectorRef.detectChanges()
        });
    }

}
