import {Component, OnInit} from '@angular/core';
import {User} from "../../model/user/user.model";
import {LoginService} from "../../service/login/login.service";
import {TEXTS} from "../../text/app.text_messages";
import {UserInfo} from "../../auth/userInfo";
import {AuthUser} from "../../model/user/user-contacts.model";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatButton, MatIconButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
    imports: [
        MatIcon,
        MatMenu,
        MatMenuTrigger,
        MatIconButton,
        MatButton,
        MatMenuItem,
        RouterLink,
        NgIf
    ],
  standalone: true
})
export class LoginComponent implements OnInit {
  public isLoggedIn = false
  public user: AuthUser | undefined = undefined
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
