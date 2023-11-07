import {CanActivateFn, Router} from '@angular/router';
import {UserInfo} from "./userInfo";
import {inject} from "@angular/core";

export const loggedInGuard: CanActivateFn = (route, state) => {
  if (UserInfo.loginStatus.value && UserInfo.currentUser !== undefined)
    return true;
  const router = inject(Router)
  return router.parseUrl('/')
};
