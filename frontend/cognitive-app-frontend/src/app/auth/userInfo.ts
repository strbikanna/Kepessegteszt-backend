import {BehaviorSubject} from "rxjs";
import {User} from "../model/user.model";
import {Role} from "../utils/constants";
import {AuthUser} from "../model/user-contacts.model";

/**
 * Global class to spread current user info and authentication data.
 */
export class UserInfo{
  public static readonly loginStatus: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
  public static currentUser: AuthUser = {
    username: "Test",
    firstName: "Clara",
    lastName: "Oswald",
    roles: ["STUDENT"],
    email: "",
    id: 1,
    contacts: [],
  }
  public static accessToken: string
  public static isAdmin(){
    return this.currentUser.roles.includes(Role.ADMIN) && this.loginStatus.value
  }
  public static canSeeInsightData(){
    return (this.currentUser.roles.includes(Role.ADMIN) || this.currentUser.roles.includes(Role.TEACHER) || this.currentUser.roles.includes(Role.SCIENTIST))
        && this.loginStatus.value
  }

  public static canSeeOthersResults(){
    return (this.currentUser.roles.includes(Role.ADMIN) || this.currentUser.roles.includes(Role.TEACHER) || this.currentUser.roles.includes(Role.SCIENTIST) || this.currentUser.roles.includes(Role.PARENT))
        && this.loginStatus.value
  }
}
