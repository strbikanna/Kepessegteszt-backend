import {Component, OnInit} from '@angular/core';
import {User} from "../model/user.model";
import {LoginService} from "./login.service";
import {UserInfo} from "../utils/userInfo";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  public isLoggedIn = false
  public user: User | undefined = undefined
  constructor(private loginService: LoginService) {}

  ngOnInit() {
    this.loginService.initAuthentication()
    this.loginService.loginStatus.subscribe(loginStatus => {
      this.isLoggedIn = loginStatus
      if(this.isLoggedIn){
        this.user = this.loginService.userInfo
      }
    });
  }

  login() {
    // UserInfo.loginStatus.next(true)
    // this.isLoggedIn = true
    // this.user = UserInfo.currentUser
    this.loginService.login()
  }

  logout() {
    this.loginService.logout()
  }
}
