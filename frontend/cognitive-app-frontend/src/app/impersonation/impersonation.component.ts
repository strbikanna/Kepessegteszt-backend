import {Component, OnInit} from '@angular/core';
import {LoginService} from "../login/login.service";
import {UserInfo} from "../utils/userInfo";
import {HttpClient} from "@angular/common/http";
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
  constructor(private loginService: LoginService, private http: HttpClient) {}
  ngOnInit(): void {
    UserInfo.loginStatus.subscribe(loginSuccess => {
      if (loginSuccess && this.hasImpersonationRole(UserInfo.currentUser.roles)) {
        this.canImpersonate = true
        this.user = UserInfo.currentUser
        this.contacts = this.getContacts()
      }
    });
  }

  signInAs(username: string){
    this.loginService.loginAs(username)
    this.canImpersonate = false
  }
  private getContacts(): Observable<User[]> {
    return this.http.get<User[]>("http://localhost:9000/user/contacts")
  }
  private hasImpersonationRole(roles: string[]): boolean {
    const impersonationRoles = roles.filter(role =>
      role.toUpperCase() === "TEACHER" ||
      role.toUpperCase() === "PARENT" ||
      role.toUpperCase() === "SCIENTIST" ||
      role.toUpperCase() === "ADMIN")
    return impersonationRoles.length > 0
  }

}
