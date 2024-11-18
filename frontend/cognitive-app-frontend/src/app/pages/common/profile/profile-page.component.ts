import {Component, OnInit} from '@angular/core';
import {UserInfo} from "../../../auth/userInfo";
import {User} from "../../../model/user.model";
import {TEXTS} from "../../../utils/app.text_messages";
import {AuthUser} from "../../../model/user-contacts.model";
import {UserDataService} from "../../../service/user-data/user-data.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent implements OnInit{
  user?: User
  email: string = ''

  text = TEXTS.user_info

  constructor(private service: UserDataService) {
  }

  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(isLoggedIn =>{
      this.email = UserInfo.currentUser.email
    });
    this.service.getUserData().subscribe(user => {
      this.user = user
    })
  }

}
