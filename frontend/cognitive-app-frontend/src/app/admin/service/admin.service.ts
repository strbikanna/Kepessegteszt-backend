import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AppConstants} from "../../utils/constants";
import {Observable, retry} from "rxjs";
import {UserForAdmin} from "../model/user-contacts.model";
import {UserInfo} from "../../auth/userInfo";

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  /**
   * Search users by name
   * only for admins
   * @param name
   */
  searchUsersByName(name: string): Observable<UserForAdmin[]> | undefined {
    if (UserInfo.isAdmin()) {
      let params = new HttpParams();
      params = params.append('nameText', name);
      return this.http.get<UserForAdmin[]>(`${AppConstants.authServerUrl}/user/search`, {params: params})
          .pipe(
              retry(3)
          );
    }
    return undefined;
  }

  getAllUsers(pageNumber?: number, pageSize?: number):Observable<UserForAdmin[]>{
    let params = new HttpParams();
    if(pageNumber !== undefined && pageSize !== undefined){
      params = params.append('pageNumber', pageNumber);
      params = params.append('pageSize', pageSize);
    }
    return this.http.get<UserForAdmin[]>(`${AppConstants.authServerUrl}/user/all`, {params: params})
        .pipe(
            retry(3)
        );
  }
  getContactsOfUser(user: UserForAdmin):Observable<UserForAdmin[]>{
    return this.http.get<UserForAdmin[]>(`${AppConstants.authServerUrl}/user/${user.id}/contacts`)
        .pipe(
            retry(3)
        );
  }
  getNumberOfUsers():Observable<number>{
    return this.http.get<number>(`${AppConstants.authServerUrl}/user/count`)
        .pipe(
            retry(3)
        );
  }
  updateUserData(user: UserForAdmin): Observable<UserForAdmin>{
    return this.http.put<UserForAdmin>(`${AppConstants.authServerUrl}/user/${user.id}`, user)
        .pipe(
            retry(3)
        );
  }
}
