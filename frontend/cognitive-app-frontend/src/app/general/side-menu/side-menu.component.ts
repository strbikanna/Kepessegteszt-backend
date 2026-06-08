import {Component, Input,} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {Observable, of} from "rxjs";
import {UserInfo} from "../../auth/userInfo";
import { MatDrawerContainer, MatDrawer } from '@angular/material/sidenav';
import { NgIf, AsyncPipe } from '@angular/common';
import { MatDivider } from '@angular/material/divider';
import { MatMenuItem } from '@angular/material/menu';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import {HasPermissionDirective} from "../../auth/has-permission.directive";
import {Permission} from "../../utils/constants";
import {HasAnyPermissionDirective} from "../../auth/has-any-permission.directive";

@Component({
    selector: 'app-side-menu',
    templateUrl: './side-menu.component.html',
    styleUrls: ['./side-menu.component.scss'],
    standalone: true,
    imports: [MatDrawerContainer, MatDrawer, NgIf, MatDivider, MatMenuItem, RouterLink, RouterLinkActive, MatIcon, AsyncPipe, HasPermissionDirective, HasAnyPermissionDirective]
})
export class SideMenuComponent extends HeaderComponent {

    @Input() isDrawerOpenObservable: Observable<boolean> = of(true);

    protected readonly UserInfo = UserInfo;
}
