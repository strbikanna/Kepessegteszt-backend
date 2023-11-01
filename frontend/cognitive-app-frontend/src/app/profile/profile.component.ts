import {Component, OnInit} from '@angular/core';
import {UserInfo} from "../auth/userInfo";
import {User} from "../model/user.model";
import {TEXTS} from "../utils/app.text_messages";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit{
  public user!: User
  text = TEXTS.user_info

  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(isLoggedIn =>{
      this.user = UserInfo.currentUser
    });
  }

}
