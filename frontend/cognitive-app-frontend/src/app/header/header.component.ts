import {Component, OnInit} from '@angular/core';
import {UserInfo} from "../utils/userInfo";
import {Role} from "../utils/constants";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit{
  loginStatus = false;
  isAdmin = false;


  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(loginSuccess => {
        this.loginStatus = loginSuccess
      this.isAdmin = UserInfo.currentUser?.roles.find(role => role.toUpperCase() === Role.ADMIN) !== undefined  && this.loginStatus
    });
  }

}