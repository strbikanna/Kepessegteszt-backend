import {CanActivateFn, Router} from '@angular/router';
import {UserInfo} from "./userInfo";
import {inject} from "@angular/core";
import {Role} from "../utils/constants";

/**
 * Guard routes to be only accessible by users permitted to edit (e.g. edit games data)
 * @param route
 * @param state
 */
export const gameManagementGuard: CanActivateFn = (route, state) => {
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
    return router.parseUrl('/')
};
