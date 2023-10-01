import {Component, OnInit} from '@angular/core';
import {User} from "../model/user.model";
import {LoginService} from "./login.service";

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
    this.loginService.login()
  }

  logout() {
    this.loginService.logout()
  }
}
