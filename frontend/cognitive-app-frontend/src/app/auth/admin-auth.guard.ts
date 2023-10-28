import { CanActivateFn } from '@angular/router';
import {User} from "../model/user.model";
import {Role} from "../utils/constants";
import {UserInfo} from "./userInfo";

export const adminAuthGuard: CanActivateFn = (route, state) => {
  return UserInfo.currentUser && hasAdminRole(UserInfo.currentUser);
};
function hasAdminRole(user: User){
  return user.roles.find(role => role.toUpperCase() === Role.ADMIN) !==undefined
}