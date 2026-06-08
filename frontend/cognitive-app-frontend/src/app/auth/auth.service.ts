import {computed, Injectable} from '@angular/core';
import {Permission, Role} from "../utils/constants";
import {UserInfo} from "./userInfo";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private userRoles = computed(() => UserInfo.currentUserSignal()?.roles ?? [])

  hasPermission(permission: Permission): boolean {
    return this.userRoles().find((r: Role) => ROLE_PERMISSIONS[r].includes(permission)) !== undefined
  }

  hasAnyPermission(permissions: Permission[]): boolean {
    return permissions.some(p => this.hasPermission(p));
  }
}

export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
  [Role.ADMIN]: [
    Permission.IMPERSONATE_USERS,
    Permission.VIEW_GAMES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON,
    Permission.VIEW_ABILITIES,
    Permission.VIEW_RECOMMENDATIONS,
    Permission.VIEW_OTHERS_RESULTS,
    Permission.VIEW_GROUPS,
    Permission.MANAGE_ABILITIES,
    Permission.MANAGE_USERS_DATA,
    Permission.MANAGE_USER_REGISTRATION,
    Permission.MANAGE_GROUPS,
    Permission.MANAGE_OTHERS_COGNITIVE_PROFILES,
    Permission.MANAGE_RECOMMENDATIONS,
    Permission.MANAGE_GAMES,
  ],
  [Role.TEACHER]: [
    Permission.IMPERSONATE_USERS,
    Permission.VIEW_GAMES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON,
    Permission.VIEW_ABILITIES,
    Permission.VIEW_RECOMMENDATIONS,
    Permission.VIEW_OTHERS_RESULTS,
    Permission.VIEW_GROUPS,
    Permission.MANAGE_USER_REGISTRATION,
    Permission.MANAGE_GROUPS,
    Permission.MANAGE_OTHERS_COGNITIVE_PROFILES,
    Permission.MANAGE_RECOMMENDATIONS,
  ],
  [Role.STUDENT]: [
    Permission.VIEW_GAMES,
    Permission.VIEW_OWN_COGNITIVE_PROFILE,
    Permission.VIEW_OWN_COGNITIVE_PROFILE_COMPARISON,
    Permission.VIEW_OWN_RESULTS,
    Permission.VIEW_GROUPS,
  ],
  [Role.SCIENTIST]: [
    Permission.IMPERSONATE_USERS,
    Permission.VIEW_GAMES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON,
    Permission.VIEW_ABILITIES,
    Permission.VIEW_RECOMMENDATIONS,
    Permission.VIEW_OTHERS_RESULTS,
    Permission.VIEW_GROUPS,
    Permission.MANAGE_ABILITIES,
    Permission.MANAGE_USER_REGISTRATION,
    Permission.MANAGE_GROUPS,
    Permission.MANAGE_OTHERS_COGNITIVE_PROFILES,
    Permission.MANAGE_RECOMMENDATIONS,
    Permission.MANAGE_GAMES,
  ],
  [Role.PARENT]: [
    Permission.IMPERSONATE_USERS,
    Permission.VIEW_GAMES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILES,
    Permission.VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON,
    Permission.VIEW_ABILITIES,
    Permission.VIEW_RECOMMENDATIONS,
    Permission.VIEW_OTHERS_RESULTS,
    Permission.VIEW_GROUPS,
    Permission.MANAGE_USER_REGISTRATION,
  ],
  [Role.TEACHER_REQUEST]: [
    Permission.VIEW_GAMES,
    Permission.VIEW_ABILITIES,
  ],
  [Role.SCIENTIST_REQUEST]: [
    Permission.VIEW_GAMES,
    Permission.VIEW_ABILITIES,
  ],
  [Role.PARENT_REQUEST]: [
    Permission.VIEW_GAMES,
    Permission.VIEW_ABILITIES,
  ],

};
