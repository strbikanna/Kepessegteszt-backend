import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable,} from "rxjs";
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {Ability} from "../../model/ability.model";
import {AppConstants} from "../../utils/constants";

@Injectable({
  providedIn: 'root'
})
export class CognitiveProfileService {
    private baseUrl = AppConstants.resourceServerUrl
    snapshotEndpoint = '/profile_snapshot'
    profileEndpoint = '/user/profile'
    inspectPath = '/inspect'

  constructor(private http: HttpClient) { }

    /**
     * returns the last three profile snapshot
     */
    getLatestProfiles(): Observable<CognitiveProfile[]>{
        const params = new HttpParams()
        params.set('pageIndex', 0)
        params.set('pageSize', 3)
        return this.http.get<CognitiveProfile[]>(`${this.baseUrl}${this.snapshotEndpoint}`, {params: params}).pipe(
            map((res: any[]) => this.convertToCognitiveProfile(res) )
        )

    }

  /**
   * returns the actual cognitive profile of the user logged in
   */
  getCurrentProfile(): Observable<CognitiveProfile>{
        return this.http.get<CognitiveProfile>(`${this.baseUrl}${this.profileEndpoint}`).pipe(
            map((res: any) => this.convertToCognitiveProfile(res.profile)[0] )
        )

  }

  /**
   * returns all saved cognitive profiles of the user logged in between the given dates
   */
  getProfilesBetween(start: Date, end: Date): Observable<CognitiveProfile[]>{
      let params = new HttpParams()
      params = params.set('startTime', start.toISOString())
      params = params.set('endTime', end.toISOString())
      return this.http.get<CognitiveProfile[]>(`${this.baseUrl}${this.snapshotEndpoint}`, {params: params}).pipe(
          map((res: any[]) => this.convertToCognitiveProfile(res) )
      )
  }
  private convertToCognitiveProfile(profileItems: any[]): CognitiveProfile[]{
      let model : CognitiveProfile[] = []
      profileItems.forEach(item => {
          item.timestamp = item.timestamp ? new Date(item.timestamp) : new Date()
          let profileAtTime = model.find(profile =>
              profile.timestamp.getFullYear() === item.timestamp.getFullYear()
              && profile.timestamp.getMonth() === item.timestamp.getMonth()
                && profile.timestamp.getDay() === item.timestamp.getDay()
          )
          if(profileAtTime){
              profileAtTime.profileItems.set(item.ability, item.abilityValue)
          }else{
                let profile = {timestamp: item.timestamp, profileItems: new Map<Ability, any>()}
                profile.profileItems.set(item.ability, item.abilityValue)
                model.push(profile)
          }
      })
      return model
  }
}
