import {environment} from "../../environments/environment";

export const AppConstants = {
    authServerUrl: environment.authServerUrl,
    resourceServerUrl: environment.resourceServerUrl,
    impersonationKey: 'impersonation',
    impersonationDisabledKey: 'disabled_impersonation',
}

export enum Role {
    STUDENT = 'STUDENT',
    TEACHER = 'TEACHER',
    ADMIN = 'ADMIN',
    SCIENTIST = 'SCIENTIST',
    PARENT = 'PARENT',
    TEACHER_REQUEST = 'TEACHER_REQUEST',
    SCIENTIST_REQUEST = 'SCIENTIST_REQUEST',
    PARENT_REQUEST = 'PARENT_REQUEST',
}

export enum Permission {
    IMPERSONATE_USERS,
    VIEW_GAMES,
    VIEW_OWN_COGNITIVE_PROFILE,
    VIEW_OTHERS_COGNITIVE_PROFILES,
    VIEW_OWN_COGNITIVE_PROFILE_COMPARISON,
    VIEW_OTHERS_COGNITIVE_PROFILE_COMPARISON,
    VIEW_ABILITIES,
    VIEW_RECOMMENDATIONS,
    VIEW_OWN_RESULTS,
    VIEW_OTHERS_RESULTS,
    VIEW_GROUPS,
    MANAGE_ABILITIES,
    MANAGE_USERS_DATA,
    MANAGE_USER_REGISTRATION,
    MANAGE_GROUPS,
    MANAGE_OTHERS_COGNITIVE_PROFILES,
    MANAGE_RECOMMENDATIONS,
    MANAGE_GAMES,
}