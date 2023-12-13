import {BehaviorSubject} from "rxjs";
import {User} from "../model/user.model";

/**
 * Global class to spread current user info and authentication data.
 */
export class UserInfo{
  public static readonly loginStatus: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
  public static currentUser: User = {
    username: "Test",
    firstName: "Clara",
    lastName: "Oswald",
    email: "clara_testuser@test.cogni",
    roles: ["STUDENT"]
  }
  public static accessToken: string
}
