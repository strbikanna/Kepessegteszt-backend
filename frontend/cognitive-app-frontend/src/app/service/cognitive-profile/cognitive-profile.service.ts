import {Injectable} from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import {catchError, map, Observable, of, retry, tap,} from "rxjs";
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {Ability} from "../../model/ability.model";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {ProfileDescription} from "../../model/profile/profile_description";
import {TEXTS} from "../../text/app.text_messages";
import {GenericProfileData} from "../../model/profile/profile_data.model";

@Injectable({
    providedIn: 'root'
})
export class CognitiveProfileService {
    snapshotEndpoint = '/profile_snapshot'
    profileEndpoint = '/user/profile'
    profileDescriptionEndpoint = '/profile_description'
    inspectPath = '/inspect'

    constructor(private http: HttpClient, private helper: SimpleHttpService) {
    }


    /**
     * returns the last 10 profile snapshot by default
     */
    getLatestProfiles(count: number = 10): Observable<CognitiveProfile[]> {
        let params = new HttpParams()
        params = params.set('pageIndex', 0)
        params = params.set('pageSize', count)
        return this.http.get<CognitiveProfile[]>(`${this.helper.baseUrl}${this.snapshotEndpoint}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any[]) => this.convertToCognitiveProfile(res))
        )

    }

    getProfileDescription(prompt?: string, username?: string): Observable<ProfileDescription>{
        let params = new HttpParams();
        if(prompt){
            params = params.set('prompt', prompt);
        }
        if(username){
            params = params.set('requestedUsername', username);
        }
        return this.http.get<ProfileDescription>(`${this.helper.baseUrl}${this.profileDescriptionEndpoint}`, {params: params}).pipe(
            retry(2),
            map(desc =>{
                if(desc.generatedText === ''){
                    desc.generatedText = TEXTS.cognitive_profile.llm.empty_description;
                }
                desc.prompt = prompt ?? '';
                return desc
            }),
            catchError(this.helper.handleHttpError)
        )
    }

    /**
     * returns the last 10 profile snapshot of the given user by default
     * @param username
     * @param count
     */
    getLatestProfilesOfOtherUser(username: string, count: number = 10) {
        let params = new HttpParams()
        params = params.set('pageIndex', 0)
        params = params.set('pageSize', count)
        params = params.set('username', username)
        return this.http.get<CognitiveProfile[]>(`${this.helper.baseUrl}${this.snapshotEndpoint}${this.inspectPath}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any[]) => this.convertToCognitiveProfile(res))
        )
    }

    /**
     * returns the actual cognitive profile of the user logged in
     */
    getCurrentProfile(): Observable<GenericProfileData[]> {
        return this.http.get<GenericProfileData[]>(`${this.helper.baseUrl}${this.profileEndpoint}`).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
        )

    }

    /**
     * returns the actual cognitive profile of the given user
     * @param username
     */
    getCurrentProfileOfOtherUser(username: string): Observable<GenericProfileData[]> {
        let params = new HttpParams()
        params = params.set('username', username)
        return this.http.get<GenericProfileData[]>(`${this.helper.baseUrl}${this.profileEndpoint}${this.inspectPath}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
        )
    }

    /**
     * returns all saved cognitive profiles of the user logged in between the given dates
     */
    getProfilesBetween(start: Date, end: Date): Observable<CognitiveProfile[]> {
        let params = new HttpParams()
        params = params.set('startTime', start.toISOString())
        params = params.set('endTime', end.toISOString())
        return this.http.get<CognitiveProfile[]>(`${this.helper.baseUrl}${this.snapshotEndpoint}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any[]) => this.convertToCognitiveProfile(res) )
        )
    }

    /**
     * returns all saved cognitive profiles of the given user between the given dates
     * @param start
     * @param end
     * @param username
     */
    getProfilesBetweenOfOtherUser(start: Date, end: Date, username: string): Observable<CognitiveProfile[]> {
        let params = new HttpParams()
        params = params.set('startTime', start.toISOString())
        params = params.set('endTime', end.toISOString())
        params = params.set('username', username)
        return this.http.get<CognitiveProfile[]>(`${this.helper.baseUrl}${this.snapshotEndpoint}${this.inspectPath}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any[]) => this.convertToCognitiveProfile(res))
        )
    }

    updateCurrentProfile(profileData: GenericProfileData[], username: string): Observable<GenericProfileData[]> {
        const validProfileData = profileData.filter(item =>  item && item.value != null && item.accuracy != null)
        return this.http.put<GenericProfileData[]>(`${this.helper.baseUrl}${this.profileEndpoint}?username=${username}`, validProfileData).pipe(
            catchError(this.helper.handleHttpError)
        )

    }

    getXp(): Observable<number>{
        return this.http.get<number>(`${this.helper.baseUrl}/user/xp`)
    }
    getXpOfUser(username: string): Observable<number>{
        return this.http.get<number>(`${this.helper.baseUrl}/user/xp/inspect?username=${username}`)
    }
    updateXpOfUser(username: string, xp: number): Observable<number>{
        return this.http.put<number>(`${this.helper.baseUrl}/user/xp?username=${username}?xp=${xp}`, {})
    }


    /**
     * converts server data to client side model
     * @param profileItems
     * @private
     */
    private convertToCognitiveProfile(profileItems: any[]): CognitiveProfile[] {
        let model: CognitiveProfile[] = []
        profileItems?.forEach(item => {
            item.timestamp = item.timestamp ? new Date(item.timestamp) : new Date()
            let profileAtTime = model.find(profile =>
                profile.timestamp.getFullYear() === item.timestamp.getFullYear()
                && profile.timestamp.getMonth() === item.timestamp.getMonth()
                && profile.timestamp.getDate() === item.timestamp.getDate()
            )
            if (profileAtTime) {
                profileAtTime.profileItems.set(item.ability, item.value)
            } else {
                let profile = {
                    timestamp: new Date(item.timestamp),
                    profileItems: new Map<Ability, any>()
                }
                profile.profileItems.set(item.ability, item.value)
                model.push(profile)
            }
        })
        return model
    }
}
