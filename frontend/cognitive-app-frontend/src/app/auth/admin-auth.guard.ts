import {CanActivateFn, Router} from '@angular/router';
import {User} from "../model/user.model";
import {Role} from "../utils/constants";
import {UserInfo} from "./userInfo";
import {inject} from "@angular/core";

export const adminAuthGuard: CanActivateFn = (route, state) => {
  if(UserInfo.currentUser && hasAdminRole(UserInfo.currentUser))
    return true
  const router = inject(Router)
  return router.parseUrl('/')
};
function hasAdminRole(user: User){
  return user.roles.find(role => role.toUpperCase() === Role.ADMIN) !==undefined
}