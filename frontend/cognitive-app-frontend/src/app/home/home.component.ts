import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {UserInfo} from "../auth/userInfo";
import {Role} from "../utils/constants";
import {imagePaths} from "../utils/app.image_resources";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

    texts = TEXTS.home
    user = UserInfo.currentUser
    protected readonly imagePaths = imagePaths;


    constructor(private changeDetectorRef: ChangeDetectorRef,) {
    }

    ngOnInit() {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            this.user = UserInfo.currentUser
            this.changeDetectorRef.detectChanges()
        });
    }

    getFeatureMessageForUser() {
        if (this.user === undefined) {
            return this.texts.login
        }
        if (this.user.roles.includes(Role.SCIENTIST)) {
            return this.texts.features_scientist
        }
        if (this.user.roles.includes(Role.TEACHER)) {
            return this.texts.features_teacher
        }
        if (this.user.roles.includes(Role.PARENT)) {
            return this.texts.features_parent
        }
        if (this.user.roles.includes(Role.STUDENT)) {
            return this.texts.features_student
        } else return ''
    }

}
