export const AppConstants = {
    authServerUrl : 'http://cognitiveapp-authentication.northeurope.azurecontainer.io',
    resourceServerUrl : 'http://localhost:8090',
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