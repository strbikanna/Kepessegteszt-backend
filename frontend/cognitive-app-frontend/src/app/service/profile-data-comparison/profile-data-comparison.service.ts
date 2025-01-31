import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {filter, map, Observable, retry} from "rxjs";
import {ProfileData} from "../../model/profile_data.model";
import {Ability, AbilityType} from "../../model/ability.model";
import {UserGroup} from "../../model/user_group.model";
import {UserFilter} from "../../common/user-filter/user-filter.model";
import {ProfileStatistics} from "../../model/profile-statistics.model";
import {ProfileDescription} from "../../model/ProfileDescription";
import {TEXTS} from "../../utils/app.text_messages";

@Injectable({
  providedIn: 'root'
})
export class ProfileDataComparisonService {

  constructor(private http: HttpClient, private httpService: SimpleHttpService) { }
  
  getProfileData(userName?: string): Observable<ProfileData[]> {
    const route = userName ? '/user/profile/inspect' : '/user/profile';
    const params = userName ? new HttpParams().set('username', userName) : new HttpParams();
    return this.http.get(this.httpService.baseUrl + route, {params: params}).pipe(
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

  getGroupsOfOtherUser(username: string){
    const params = new HttpParams().set('username', username);
    return this.http.get<UserGroup[]>(this.httpService.baseUrl + '/user/groups/inspect', {params: params});
  }

  getProfileDataOfGroup(filter: UserFilter | undefined, aggregationType: 'average'| 'min' | 'max' | 'sum', username?: string): Observable<ProfileData[]> {
    let params = new HttpParams()
        .set('aggregationMode', aggregationType);
    if(filter?.userGroupId){
        params = params.set('userGroupId', filter.userGroupId);
    }
    if(username){
        params = params.set('username', username);
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
    const route = username ? '/user/group_profile/aggregate/inspect' : '/user/group_profile/aggregate';
    return this.http.post<ProfileData[]>(`${this.httpService.baseUrl}${route}`, userFilter, {params: params}).pipe(
        retry(3),
    )
  }

  getProfileStatisticsOfGroup(filter: UserFilter | undefined, username: string): Observable<ProfileStatistics[]> {
    let params = new HttpParams();
    if(filter?.userGroupId){
        params = params.set('userGroupId', filter.userGroupId);
    }
    params = params.set('username', username);
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
    return this.http.post<ProfileStatistics[]>(`${this.httpService.baseUrl}/user/group_profile/statistics`, userFilter, {params: params}).pipe(
        retry(3),
    )
  }

  getComparisonDescription(filter?: UserFilter, prompt?: string, username?: string): Observable<ProfileDescription> {
    let params = new HttpParams();
    if(prompt){
      params = params.set('prompt', prompt);
    }
    if(username){
      params = params.set('requestedUsername', username);
    }
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
    return this.http.post<ProfileDescription>(this.httpService.baseUrl + '/user/profile/abilities-as-text-to-group',userFilter, {params: params}).pipe(
        map((response: any) => {
          if(response.abilitiesAsText === ""){
            response.abilitiesAsText = TEXTS.cognitive_profile.llm.empty_description;
          }
            return response;
        })
    );
  }

}
