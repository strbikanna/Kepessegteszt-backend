export const AppConstants = {
    authServerUrl : 'http://localhost:9000',
    resourceServerUrl : 'http://localhost:8090',
    impersonationKey: 'impersonation',
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