import { Injectable } from '@angular/core';
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {User} from "../model/user.model";
import {BehaviorSubject, Observable} from "rxjs";
import {UserInfo} from "../utils/userInfo";
import {HttpClient} from "@angular/common/http";
import {AppConstants, Role} from "../utils/constants";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  public userInfo: User | undefined = undefined
  public loginStatus: BehaviorSubject<boolean> = new BehaviorSubject(false)

  constructor(private oidcSecurityService: OidcSecurityService, private http: HttpClient) { }

  initAuthentication(){
    this.oidcSecurityService
      .checkAuth()
      .subscribe((loginResponse: LoginResponse) => {
        const {isAuthenticated, userData, accessToken, idToken, configId} = loginResponse;
        if(isAuthenticated){
          this.userInfo = this.convertUserData(userData)
          UserInfo.currentUser = this.userInfo
          UserInfo.accessToken = accessToken
        }
        this.loginStatus.next(isAuthenticated)
        UserInfo.loginStatus.next(isAuthenticated)
      });
  }
  login() {
    this.oidcSecurityService.authorize();
  }
  loginAs(username: string){
    console.log(username)
    this.oidcSecurityService.authorize('baseConfig', {customParams: { 'act_as': username } })
  }

  logout() {
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
  private convertUserData(userInfoResponse: any): User{
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
