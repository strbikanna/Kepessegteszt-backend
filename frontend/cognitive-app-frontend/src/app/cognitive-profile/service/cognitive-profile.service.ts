import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map, Observable, of, retry,} from "rxjs";
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {Ability} from "../../model/ability.model";
import {AppConstants} from "../../utils/constants";
import {User} from "../../model/user.model";
import {SimpleHttpService} from "../../utils/simple-http.service";

@Injectable({
    providedIn: 'root'
})
export class CognitiveProfileService {
    snapshotEndpoint = '/profile_snapshot'
    profileEndpoint = '/user/profile'
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
    getCurrentProfile(): Observable<CognitiveProfile> {
        return this.http.get<CognitiveProfile>(`${this.helper.baseUrl}${this.profileEndpoint}`).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any) => this.convertToCognitiveProfile(res)[0])
        )

    }

    /**
     * returns the actual cognitive profile of the given user
     * @param username
     */
    getCurrentProfileOfOtherUser(username: string): Observable<CognitiveProfile> {
        let params = new HttpParams()
        params = params.set('username', username)
        return this.http.get<CognitiveProfile>(`${this.helper.baseUrl}${this.profileEndpoint}${this.inspectPath}`, {params: params}).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
            map((res: any) => this.convertToCognitiveProfile(res.profile)[0])
        )
    }

    /**
     * returns all saved cognitive profiles of the user logged in between the given dates
     */
    getProfilesBetween(start: Date, end: Date): Observable<CognitiveProfile[]> {
        let params = new HttpParams()
        params = params.set('startTime', start.toISOString())
        params = params.set('endTime', end.toISOString())
        //
        return this.sample.pipe(
            map((res: any) => this.convertToCognitiveProfile(res))
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

    /**
     * returns all contacts of user logged in
     */
    getContacts(): Observable<User[]> {
        return this.http.get<User[]>(`${AppConstants.authServerUrl}/user/impersonation_contacts`).pipe(
            retry(3),
            catchError(this.helper.handleHttpError),
        )
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

    private sample = of(
        [
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 0.78,
                "timestamp": "2023-11-01 10:00:00"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 0.82,
                "timestamp": "2023-11-21 14:55:00"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 0.91,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.7,
                "timestamp": "2023-11-01 10:00:00"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.76,
                "timestamp": "2023-11-21 14:55:00"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.9,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2023-11-01 10:00:00"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2023-11-21 14:55:00"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 0.99,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 0.9,
                "timestamp": "2023-11-01 10:00:00"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 0.97,
                "timestamp": "2023-11-21 14:55:00"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 0.95,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.05,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 0.96,
                "timestamp": "2023-12-05 16:15:00"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-12 15:02:24"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-13 12:29:45"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-14 09:42:51"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-16 15:32:08"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-18 13:58:11"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-19 09:41:11"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-22 08:30:16"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-23 13:07:26"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-24 11:07:15"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-25 13:13:30"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-26 12:29:13"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-27 19:38:21"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-28 14:43:24"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-04-29 09:38:34"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-01 19:21:20"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-02 09:24:42"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-03 11:26:13"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-04 17:31:55"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-05 21:30:46"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-06 08:40:17"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-07 11:57:16"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-08 21:11:28"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-09 12:05:23"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-10 09:35:19"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-11 14:31:38"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.997,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-12 10:37:14"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-13 07:54:25"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-14 13:15:16"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-18 10:11:46"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-20 13:12:31"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-23 16:35:08"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-24 20:04:32"
            },
            {
                "ability": {
                    "code": "Gf",
                    "name": "Fluid reasoning",
                    "description": "Képesség új problémák megoldására, logikai következtetésre és absztrakt gondolkodásra.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-25 11:34:42"
            },
            {
                "ability": {
                    "code": "Gsm",
                    "name": "Short term memory",
                    "description": "Képesség a rövidtávú emlékezet  használatára.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-25 11:34:42"
            },
            {
                "ability": {
                    "code": "Ga",
                    "name": "Auditory processing",
                    "description": "Képesség hangok érzékelésére, a hallott információk feldolgozására.",
                    "type": "FLOATING"
                },
                "value": 1.151,
                "timestamp": "2024-05-25 11:34:42"
            },
            {
                "ability": {
                    "code": "Glr",
                    "name": "Long-term memory",
                    "description": "Képesség a korábban megszerzett információk felidézésére. Az információ hosszú távú tárolására és lekérdezésére.",
                    "type": "FLOATING"
                },
                "value": 1.0,
                "timestamp": "2024-05-25 11:34:42"
            },
            {
                "ability": {
                    "code": "Gv",
                    "name": "Visual processing",
                    "description": "Képesség a vizuális információk feldolgozására, a térbeli tájékozódásra.",
                    "type": "FLOATING"
                },
                "value": 1.002,
                "timestamp": "2024-05-25 11:34:42"
            },
            {
                "ability": {
                    "code": "Gc",
                    "name": "Crystallized intelligence",
                    "description": "Képesség a korábban megszerzett tudás felhasználására.",
                    "type": "FLOATING"
                },
                "value": 0.8,
                "timestamp": "2024-05-25 11:34:42"
            }
        ]
    )
}
