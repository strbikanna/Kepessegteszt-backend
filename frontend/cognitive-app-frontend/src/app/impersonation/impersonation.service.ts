import { Injectable } from '@angular/core';
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {HttpClient} from "@angular/common/http";
import {UserInfo} from "../auth/userInfo";
import {User} from "../model/user.model";
import {BehaviorSubject, filter, map, Observable} from "rxjs";
import {AppConstants, Role} from "../utils/constants";

@Injectable({
  providedIn: 'root'
})
export class ImpersonationService {

  public userInfo: User | undefined = UserInfo.currentUser
  public loginStatus: BehaviorSubject<boolean> = UserInfo.loginStatus

  private readonly BASE_CONFIG_ID = 'baseConfig'

  constructor(private oidcSecurityService: OidcSecurityService, private http: HttpClient) { }

  initAuthentication(){
    this.oidcSecurityService
        .checkAuth(undefined, this.BASE_CONFIG_ID)
        .subscribe((loginResponse: LoginResponse) => {
          const {isAuthenticated, userData, accessToken, idToken, configId} = loginResponse;
          if(isAuthenticated){
            console.log('User impersonation authentication successful')
            UserInfo.currentUser = this.convertUserData(userData)
            UserInfo.accessToken = accessToken
          }
          UserInfo.loginStatus.next(isAuthenticated)
        });
  }
  loginAs(username: string){
    console.log('Performing login as:'+username)
    this.oidcSecurityService.authorize(this.BASE_CONFIG_ID, {customParams: { 'act_as': username } })
  }
  getContacts(): Observable<User[]> {
    return this.http.get<User[]>(`${AppConstants.authServerUrl}/user/impersonation_contacts`)
        .pipe(
            map(contacts => contacts.filter(contact => contact.roles.includes(Role.STUDENT) || contact.roles.includes("STUDENT")))
        )
  }
  hasImpersonationRole(roles: string[]): boolean {
    const impersonationRoles = roles.filter(role =>
        role.toUpperCase() === Role.TEACHER ||
        role.toUpperCase() === Role.ADMIN ||
        role.toUpperCase() === Role.SCIENTIST ||
        role.toUpperCase() === Role.PARENT)
    return impersonationRoles.length > 0
  }
  private convertUserData(userInfoResponse: any): User {
    console.log('Parsing userinfo: ')
    console.log(userInfoResponse)

    const user : User = {
      username: userInfoResponse.sub,
      firstName: userInfoResponse.family_name,
      lastName: userInfoResponse.given_name,
      email: userInfoResponse.email,
      roles: userInfoResponse.roles,
    };

    user.roles = user.roles.map(role => {
      let roleName = role
      switch (role) {
        case Role.SCIENTIST_REQUEST: roleName = "requested scientist"
          break
        case Role.TEACHER_REQUEST : roleName = "requested teacher"
          break
        case Role.PARENT_REQUEST : roleName = "requested parent"
          break
      }
      return roleName.toLowerCase()
    })
    return user;
  }
}
