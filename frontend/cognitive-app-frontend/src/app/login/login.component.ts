import {Component, OnInit} from '@angular/core';
import {User} from "../model/user.model";
import {LoginService} from "./login.service";
import {TEXTS} from "../utils/app.text_messages";
import {UserInfo} from "../auth/userInfo";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  public isLoggedIn = false
  public user: User | undefined = undefined
  text = TEXTS.menu
  constructor(private loginService: LoginService) {}

  ngOnInit() {
    this.loginService.initAuthentication()
    UserInfo.loginStatus.subscribe(loginStatus => {
      this.isLoggedIn = loginStatus
      if(this.isLoggedIn){
        this.user = UserInfo.currentUser
      }
    });
  }

  login() {
    this.loginService.login()
  }

  logout() {
    this.loginService.logout()
  }
}
