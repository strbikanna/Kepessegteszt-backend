import {environment} from "../../environments/environment";

export const AppConstants = {
    authServerUrl : environment.authServerUrl,
    resourceServerUrl : environment.resourceServerUrl,
    impersonationKey: 'impersonation',
    impersonationDisabledKey: 'disabled_impersonation',
}
export const enum Role {
    STUDENT = 'STUDENT',
    TEACHER = 'TEACHER',
    ADMIN = 'ADMIN',
    SCIENTIST = 'SCIENTIST',
    PARENT = 'PARENT',
     TEACHER_REQUEST = 'TEACHER_REQUEST',
    SCIENTIST_REQUEST = 'SCIENTIST_REQUEST',
    PARENT_REQUEST = 'PARENT_REQUEST',
}