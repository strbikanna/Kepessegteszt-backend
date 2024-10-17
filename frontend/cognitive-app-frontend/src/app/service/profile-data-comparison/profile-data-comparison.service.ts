import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {filter, map, Observable, retry} from "rxjs";
import {ProfileData} from "../../model/profile_data.model";
import {Ability, AbilityType} from "../../model/ability.model";
import {UserGroup} from "../../model/user_group.model";
import {UserFilter} from "../../common/user-filter/user-filter.model";

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
                value: item.value
              }
                return profileData;
            })
        })
    )
  }

  getGroupsOfUser() : Observable<UserGroup[]>{
    return this.http.get<UserGroup[]>(this.httpService.baseUrl + '/user/groups');
  }

  getProfileDataOfGroup(filter: UserFilter | undefined, aggregationType: 'average'| 'min' | 'max' | 'sum'): Observable<ProfileData[]> {
    let params = new HttpParams()
        .set('aggregationMode', aggregationType);
    if(filter?.userGroupId){
        params = params.set('userGroupId', filter.userGroupId);
    }
    let userFilter = null;
    if(filter){
      userFilter ={
        ageMin: filter.ageMin,
        ageMax: filter.ageMax,
        addressCity: filter.addressCity,
        addressZip: filter.addressZip,
        abilityFilter: filter.abilityFilter
      }
    }
    return this.http.post<ProfileData[]>(`${this.httpService.baseUrl}/user/group_profile/aggregate`, userFilter, {params: params}).pipe(
        retry(3),
    )
  }

}
