import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AppConstants} from "../../utils/constants";
import {Observable} from "rxjs";
import {UserForAdmin} from "../model/user-contacts.model";

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  getAllUsers(pageNumber?: number, pageSize?: number):Observable<UserForAdmin[]>{
    let params = new HttpParams();
    if(pageNumber !== undefined && pageSize !== undefined){
      params = params.append('pageNumber', pageNumber);
      params = params.append('pageSize', pageSize);
    }
    return this.http.get<UserForAdmin[]>(`${AppConstants.authServerUrl}/user/all`, {params: params});
  }
  getContactsOfUser(user: UserForAdmin):Observable<UserForAdmin[]>{
    return this.http.get<UserForAdmin[]>(`${AppConstants.authServerUrl}/user/${user.id}/contacts`);
  }
  getNumberOfUsers():Observable<number>{
    return this.http.get<number>(`${AppConstants.authServerUrl}/user/count`);
  }
  updateUserData(user: UserForAdmin): Observable<UserForAdmin>{
    return this.http.put<UserForAdmin>(`${AppConstants.authServerUrl}/user/${user.id}`, user);
  }
}
