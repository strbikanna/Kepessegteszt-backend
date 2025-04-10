import {ChangeDetectorRef, Component, EventEmitter, HostListener, OnInit, Output} from '@angular/core';
import {UserInfo} from "../../auth/userInfo";
import {Role} from "../../utils/constants";
import {TEXTS} from "../../text/app.text_messages";
import {imagePaths} from "../../utils/app.image_resources";


@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})

export class HeaderComponent implements OnInit{

    @Output() drawerStateChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

    private drawerState: boolean = false;

    loginStatus = false;
    isAdmin = false;
    text = TEXTS.menu;
    isTeacher = false;
    isScientist = false;
    isStudent = false;
    isParent = false;
    protected readonly imagePaths = imagePaths;
    windowWidth!: number;

    constructor(private changeDetectorRef: ChangeDetectorRef,) {  }

    ngOnInit(): void {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            this.loginStatus = loginSuccess
            this.isAdmin = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.ADMIN) !== undefined && loginSuccess
            this.isStudent = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.STUDENT) !== undefined && loginSuccess
            this.isTeacher = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.TEACHER) !== undefined  && this.loginStatus
            this.isScientist = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.SCIENTIST) !== undefined  && this.loginStatus
            this.isParent = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.PARENT) !== undefined  && this.loginStatus
            this.changeDetectorRef.detectChanges()
        });
        this.windowWidth = window.innerWidth;
    }

    @HostListener('window:resize', ['$event'])
    onResize(event: any) {
        this.windowWidth = window.innerWidth;
    }

    changeDrawerState() {
        this.drawerState = !this.drawerState;
        this.drawerStateChanged.emit(this.drawerState);
    }

    displayMobileMenu(){
        return this.windowWidth < 768
    }

    hasAccessToOwnCognitiveProfile(){
        return this.isStudent
    }

    hasAccessToAdminCognitiveProfile(){
        return this.isAdmin || this.isTeacher || this.isScientist || this.isParent
    }
    hasAccessToAdminCognitiveProfileCompare(){
        return this.hasAccessToAdminCognitiveProfile()
    }
    hasAccessToOwnCognitiveProfileCompare(){
        return this.isStudent
    }
    hasAccessToUserRegistration(){
        return this.isAdmin || this.isTeacher || this.isScientist || this.isParent
    }
    hasAccessToUserManagement(){
        return this.isAdmin
    }
    hasAccessToGroupManagement(){
        return this.isAdmin || this.isTeacher || this.isScientist
    }
    hasAccessToGameManagement(){
        return this.isAdmin || this.isScientist
    }
    hasAccessToRecommendations(){
        return this.isAdmin || this.isScientist || this.isTeacher
    }

}
