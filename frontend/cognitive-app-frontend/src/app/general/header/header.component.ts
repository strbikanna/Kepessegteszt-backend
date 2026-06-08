import {ChangeDetectorRef, Component, EventEmitter, HostListener, OnInit, Output} from '@angular/core';
import {UserInfo} from "../../auth/userInfo";
import {Permission, Role} from "../../utils/constants";
import {TEXTS} from "../../text/app.text_messages";
import {imagePaths} from "../../utils/app.image_resources";
import { MatToolbar } from '@angular/material/toolbar';
import { NgIf } from '@angular/common';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatMenuTrigger, MatMenu, MatMenuItem } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { ImpersonationHeaderComponent } from '../impersonation-header/impersonation-header.component';
import { LoginComponent } from '../login/login.component';
import {HasPermissionDirective} from "../../auth/has-permission.directive";
import {HasAnyPermissionDirective} from "../../auth/has-any-permission.directive";


@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss'],
    standalone: true,
    imports: [MatToolbar, NgIf, MatIconButton, MatMenuTrigger, MatIcon, RouterLink, MatButton, MatMenu, MatMenuItem, RouterLinkActive, ImpersonationHeaderComponent, LoginComponent, HasPermissionDirective, HasAnyPermissionDirective]
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
            this.isTeacher = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.TEACHER) !== undefined  && loginSuccess
            this.isScientist = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.SCIENTIST) !== undefined  && loginSuccess
            this.isParent = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.PARENT) !== undefined  && loginSuccess
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
    hasAccessToCognitiveProfileEdit(){
        return this.isAdmin
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
    hasAccessToGames(){
        return this.isStudent || this.isParent || this.isTeacher
    }
    hasAccessToRecommendations(){
        return this.isAdmin || this.isScientist || this.isTeacher
    }

    protected readonly Permission = Permission;
}
