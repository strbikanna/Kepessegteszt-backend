import {Component, OnInit} from '@angular/core';
import {LoginService} from "../login/login.service";
import {UserInfo} from "../auth/userInfo";
import {User} from "../model/user.model";
import {Observable} from "rxjs";
import {TEXTS} from "../utils/app.text_messages";
import {AppConstants} from "../utils/constants";

@Component({
  selector: 'app-impersonation',
  templateUrl: './impersonation.component.html',
  styleUrls: ['./impersonation.component.scss']
})
export class ImpersonationComponent implements OnInit {

  public contacts: Observable<User[]> = new Observable<User[]>()
  public canImpersonate = false
  public user: User | undefined = undefined
  text= TEXTS.impersonation
  private storageKey = AppConstants.impersonationDisabledKey
  constructor(private impersonationService: LoginService) {}

  /**
   * Checks if user has impersonation role and if so, gets the contacts list
   */
  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(loginSuccess => {
      if (loginSuccess && this.impersonationService.hasImpersonationRole(UserInfo.currentUser.roles) && !this.isImpersonationDisabled()) {
        this.canImpersonate = true
        this.user = UserInfo.currentUser
        this.contacts = this.impersonationService.getContacts()
      }
    });
  }

  /**
   * Initiates impersonation in the name of chosen contact user
   * @param username
   */
  signInAs(username: string){
    this.impersonationService.loginAs(username)
    this.canImpersonate = false
  }

  isImpersonationDisabled(){
    return sessionStorage.getItem(this.storageKey) === 'true'
  }
  disableImpersonation(){
    sessionStorage.setItem(this.storageKey, 'true')
    this.canImpersonate = false
  }
}
