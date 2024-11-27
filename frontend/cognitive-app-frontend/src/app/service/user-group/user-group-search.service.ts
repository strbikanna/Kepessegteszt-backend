import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {map, Observable} from "rxjs";
import {User} from "../../model/user.model";
import {UserGroup} from "../../model/user_group.model";

@Injectable({
  providedIn: 'root'
})
export class UserGroupSearchService {

  constructor(private http: HttpClient, private httpService: SimpleHttpService) {
  }
  searchMembers(groupIds: number[], name: string): Observable<User[]> {
    return this.http.post<User[]>(`${this.httpService.baseUrl}/user_group/members/search?name=${name}`, groupIds);
  }
  getAllUserGroups(): Observable<UserGroup[]> {
    return this.http.get<UserGroup[]>(`${this.httpService.baseUrl}/user_group/all`).pipe(
        map(groups => groups.map(this.convertUserGroup))
    );
  }
  getAllUsersToSee(): Observable<User[]> {
    return this.http.get<User[]>(`${this.httpService.baseUrl}/user_group/users_to_see?pageIndex=0&pageSize=30`);
  }

  private convertUserGroup(userGroup: any): UserGroup {
    if(userGroup.organization) {
      userGroup.organizationName = userGroup.organization.name
    }
    return userGroup
  }
}
