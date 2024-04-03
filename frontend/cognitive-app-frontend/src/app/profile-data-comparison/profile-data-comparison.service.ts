import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../utils/simple-http.service";
import {filter, map, Observable, retry} from "rxjs";
import {ProfileData} from "../model/profile_data.model";
import {AbilityType} from "../model/ability.model";
import {UserGroup} from "../model/user_group.model";

@Injectable({
  providedIn: 'root'
})
export class ProfileDataComparisonService {

  constructor(private http: HttpClient, private httpService: SimpleHttpService) { }
  
  getProfileData(): Observable<ProfileData[]> {
    return this.http.get(this.httpService.baseUrl + '/user/profile').pipe(
        map((response: any) =>{
            return response.filter(
                (item: any) => item.ability.type === AbilityType.FLOAT.valueOf()
            )
                .map((item : any) => {
              const profileData: ProfileData = {
                ability: {
                  code: item.ability.code,
                  name: item.ability.name,
                  description: item.ability.description,
                  type: item.ability.type
                },
                value: item.abilityValue
              }
                return profileData;
            })
        })
    )
  }

  getGroupsOfUser() : Observable<UserGroup[]>{
    return this.http.get<UserGroup[]>(this.httpService.baseUrl + '/user/groups');
  }

  getProfileDataOfGroup(groupId: number, aggregationType: 'average'| 'min' | 'max' | 'sum'): Observable<ProfileData[]> {
    const params = new HttpParams()
        .set('groupId', groupId)
        .set('aggregationMode', aggregationType);
    console.log('getting group data')
    return this.http.get<ProfileData[]>(`${this.httpService.baseUrl}/user/group_profile/aggregate`, {params: params}).pipe(
        retry(3),
    )
  }


}
