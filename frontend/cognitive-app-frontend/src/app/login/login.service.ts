import { Injectable } from '@angular/core';
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {User} from "../model/user.model";
import {BehaviorSubject} from "rxjs";
import {UserInfo} from "../utils/userInfo";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  public userInfo: User | undefined = undefined
  public loginStatus: BehaviorSubject<boolean> = new BehaviorSubject(false)

  constructor(private oidcSecurityService: OidcSecurityService) { }

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
        case "SCIENTIST_REQUEST" : roleName = "requested scientist"
          break
        case "TEACHER_REQUEST" : roleName = "requested teacher"
          break
        case "PARENT_REQUEST" : roleName = "requested parent"
          break
      }
      return roleName.toLowerCase()
    })
    return user;
  }
}
