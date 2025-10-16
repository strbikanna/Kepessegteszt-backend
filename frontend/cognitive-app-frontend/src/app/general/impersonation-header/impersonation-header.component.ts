import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../../model/user/user.model";
import {UserInfo} from "../../auth/userInfo";
import {TEXTS} from "../../text/app.text_messages";
import {AppConstants, Role} from "../../utils/constants";
import {LoginService} from "../login/login.service";
import {NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

@Component({
    selector: 'app-impersonation-header',
    standalone: true,
    imports: [
        NgIf,
        MatButton,
        MatIcon
    ],
    templateUrl: './impersonation-header.component.html',
    styleUrl: './impersonation-header.component.scss'
})
export class ImpersonationHeaderComponent implements OnInit {

    public contacts: Observable<User[]> = new Observable<User[]>()
    public user: User | undefined = UserInfo.currentUser
    text = TEXTS.impersonation
    isImpersonationActive = false
    private storageKey = AppConstants.impersonationKey

    constructor(private service: LoginService) {
    }

    /**
     * Checks if user is signed in with impersonation
     */
    ngOnInit(): void {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            if (loginSuccess) {
                this.user = UserInfo.currentUser
                this.isImpersonationActive = sessionStorage.getItem(this.storageKey) === 'true'
            }
        });
    }

    /**
     * Logs out from impersonated account and back to own account
     */
    backToOwnAccount() {
        sessionStorage.removeItem(this.storageKey)
        this.service.login()
    }

}
