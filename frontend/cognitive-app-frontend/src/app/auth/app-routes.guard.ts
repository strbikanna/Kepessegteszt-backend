import {ActivatedRouteSnapshot, CanActivate, CanActivateFn, Router} from '@angular/router';
import {UserInfo} from "./userInfo";
import {inject, Injectable} from "@angular/core";
import {Permission, Role} from "../utils/constants";
import {AuthService} from "./auth.service";


@Injectable({ providedIn: 'root' })
export class PermissionGuard implements CanActivate {

    constructor(private auth: AuthService) {}

    canActivate(route: ActivatedRouteSnapshot): boolean {
        const requiredPermissions = route.data['permissions'] as Permission[];
        return this.auth.hasAnyPermission(requiredPermissions);
    }
}

/**
 * Guard routes to be only accessible by users permitted to edit (e.g. edit games data)
 * @param route
 * @param state
 */
export const appRoutesGuard: CanActivateFn = (route, state) => {
    if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined &&
        (UserInfo.currentUser.roles.includes(Role.SCIENTIST) || UserInfo.currentUser.roles.includes(Role.ADMIN))
    )
        return true;
    const router = inject(Router)
    return router.parseUrl('/')
};

export const groupManagementGuard: CanActivateFn = (route, state) => {
    if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined &&
        (UserInfo.currentUser.roles.includes(Role.SCIENTIST) || UserInfo.currentUser.roles.includes(Role.ADMIN) || UserInfo.currentUser.roles.includes(Role.TEACHER))
    )
        return true;
    const router = inject(Router)
    return router.parseUrl('/')
};

export const recommendationGuard: CanActivateFn = (route, state) => {
    if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined &&
        (UserInfo.currentUser.roles.includes(Role.SCIENTIST) || UserInfo.currentUser.roles.includes(Role.ADMIN) || UserInfo.currentUser.roles.includes(Role.TEACHER))
    )
        return true;
    const router = inject(Router)
    return router.parseUrl('/')
};

export const adminCognitiveProfileGuard: CanActivateFn = (route, state) => {
    if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined &&
        (UserInfo.currentUser.roles.includes(Role.SCIENTIST) ||
            UserInfo.currentUser.roles.includes(Role.ADMIN) ||
            UserInfo.currentUser.roles.includes(Role.TEACHER) ||
            UserInfo.currentUser.roles.includes(Role.PARENT)
        )
    )
        return true;
    const router = inject(Router)
    return router.parseUrl('/cognitive-profile')
};

export const cognitiveProfileEditGuard: CanActivateFn = (route, state) => {
    if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined &&
        (UserInfo.currentUser.roles.includes(Role.SCIENTIST) ||
            UserInfo.currentUser.roles.includes(Role.ADMIN) ||
            UserInfo.currentUser.roles.includes(Role.TEACHER))
    )
        return true;
    const router = inject(Router)
    return router.parseUrl('/')
};
