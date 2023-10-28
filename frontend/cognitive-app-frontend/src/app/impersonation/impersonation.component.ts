import {Component, OnInit} from '@angular/core';
import {LoginService} from "../login/login.service";
import {UserInfo} from "../auth/userInfo";
import {User} from "../model/user.model";
import {Observable} from "rxjs";

@Component({
  selector: 'app-impersonation',
  templateUrl: './impersonation.component.html',
  styleUrls: ['./impersonation.component.scss']
})
export class ImpersonationComponent implements OnInit {

  public contacts: Observable<User[]> = new Observable<User[]>()
  public canImpersonate = false
  public user: User | undefined = undefined
  constructor(private impersonationService: LoginService) {}
  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(loginSuccess => {
      if (loginSuccess && this.impersonationService.hasImpersonationRole(UserInfo.currentUser.roles)) {
        this.canImpersonate = true
        this.user = UserInfo.currentUser
        this.contacts = this.impersonationService.getContacts()
      }
    });
  }

  signInAs(username: string){
    this.impersonationService.loginAs(username)
    this.canImpersonate = false
  }


}
