import {Component, OnInit} from '@angular/core';
import {UserInfo} from "../../../auth/userInfo";
import {User} from "../../../model/user.model";
import {TEXTS} from "../../../utils/app.text_messages";

@Component({
  selector: 'app-profile',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent implements OnInit{
  public user!: User
  text = TEXTS.user_info

  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(isLoggedIn =>{
      this.user = UserInfo.currentUser
    });
  }

}
