import {Injectable} from '@angular/core';
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {User} from "../../model/user.model";
import { Observable} from "rxjs";
import {UserInfo} from "../../auth/userInfo";
import {HttpClient} from "@angular/common/http";
import {AppConstants, Role} from "../../utils/constants";
import {AuthUser} from "../../model/user-contacts.model";


@Injectable({
    providedIn: 'root'
})
export class LoginService {

    private readonly BASE_CONFIG_ID = 'baseConfig'

    constructor(private oidcSecurityService: OidcSecurityService, private http: HttpClient) {}

    /**
     * Handles base config callback with auth code and retrieves token, fills userinfo after successful authentication.
     */
    initAuthentication() {
        this.handleAuthCallback(this.BASE_CONFIG_ID)
        this.oidcSecurityService.isAuthenticated(this.BASE_CONFIG_ID).subscribe(authenticated => {
            if(authenticated){
                this.fillUserInfo()
            }
        })
    }

    private handleAuthCallback(configId: string) {
        this.oidcSecurityService
            .checkAuth(undefined, configId)
            .subscribe((loginResponse: LoginResponse) => {
                console.log('Handled baseconfig auth callback.')
                const {isAuthenticated, userData, accessToken, idToken, configId} = loginResponse;
                if (isAuthenticated) {
                    console.log('User authentication successful')
                    UserInfo.currentUser = this.convertUserData(userData)
                    UserInfo.accessToken = accessToken
                }
                UserInfo.loginStatus.next(isAuthenticated)
            });
    }

    login() {
        this.oidcSecurityService.authorize(this.BASE_CONFIG_ID);
    }

    /**
     * Logs in as another user, if the current user has the impersonation role.
     * @param username
     */
    loginAs(username: string) {
        sessionStorage.setItem(AppConstants.impersonationKey, 'true')
        this.oidcSecurityService.logoffLocal(this.BASE_CONFIG_ID)
        this.oidcSecurityService.authorize(this.BASE_CONFIG_ID, {customParams: {'act_as': username}})
    }

    /**
     * Logs out the current user and removes the impersonation flag.
     */
    logout() {
        sessionStorage.removeItem(AppConstants.impersonationKey)
        sessionStorage.removeItem(AppConstants.impersonationDisabledKey)
        this.oidcSecurityService
            .logoff()
            .subscribe((result) => {
                UserInfo.loginStatus.next(false)
                console.log(result)
            });
    }

    getContacts(): Observable<User[]> {
        return this.http.get<User[]>(`${AppConstants.authServerUrl}/user/impersonation_contacts`)
    }

    hasImpersonationRole(roles: string[]): boolean {
        const impersonationRoles = roles.filter(role =>
            role.toUpperCase() === Role.TEACHER ||
            role.toUpperCase() === Role.ADMIN ||
            role.toUpperCase() === Role.SCIENTIST ||
            role.toUpperCase() === Role.PARENT)
        return impersonationRoles.length > 0
    }

    private convertUserData(userInfoResponse: any): AuthUser {
        console.log('Parsing userinfo: ')
        console.log(userInfoResponse)

        const user: AuthUser = {
            id: userInfoResponse.id,
            username: userInfoResponse.sub,
            firstName: userInfoResponse.family_name,
            lastName: userInfoResponse.given_name,
            email: userInfoResponse.email,
            roles: userInfoResponse.roles.map((role: string) => role.toUpperCase()),
            contacts: userInfoResponse.contacts
        };

        user.roles = user.roles.map(role => {
            let roleName = role
            switch (role) {
                case Role.SCIENTIST_REQUEST:
                    roleName = "requested scientist"
                    break
                case Role.TEACHER_REQUEST :
                    roleName = "requested teacher"
                    break
                case Role.PARENT_REQUEST :
                    roleName = "requested parent"
                    break
            }
            return roleName.toUpperCase()
        })
        return user;
    }

    private fillUserInfo() {
        this.oidcSecurityService.getUserData(this.BASE_CONFIG_ID).subscribe(userData => {
            if (userData) {
                UserInfo.currentUser = this.convertUserData(userData)
                UserInfo.loginStatus.next(true)
            }
        });
        this.oidcSecurityService.getAccessToken(this.BASE_CONFIG_ID).subscribe(token => {
            UserInfo.accessToken = token;
            UserInfo.loginStatus.next(true)
        });
    }
}
