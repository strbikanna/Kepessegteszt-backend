import {Injectable} from '@angular/core';
import {Observable, retry} from "rxjs";
import {AuthUser} from "../../model/user-contacts.model";
import {HttpClient, HttpParams} from "@angular/common/http";
import {AppConstants} from "../../utils/constants";
import {UserInfo} from "../../auth/userInfo";

@Injectable({
    providedIn: 'root'
})
export class ContactService {

    constructor(private http: HttpClient) {
    }

    getContactsToShow(user: AuthUser, onlyContacts: boolean = false): Observable<AuthUser[]> {
        if (UserInfo.isAdmin() && !onlyContacts) {
            return this.getAllUsers();
        }
        return this.getContactsOfUser(user.id);
    }

    getCurrUser(): Observable<AuthUser> {
        return this.http.get<AuthUser>(`${AppConstants.authServerUrl}/user/me`);
    }

    private getAllUsers(pageNumber?: number, pageSize?: number): Observable<AuthUser[]> {
        let params = new HttpParams();
        if (pageNumber !== undefined && pageSize !== undefined) {
            params = params.append('pageNumber', pageNumber);
            params = params.append('pageSize', pageSize);
        }
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/user/all`, {params: params})
            .pipe(
                retry(3)
            );
    }

    private getContactsOfUser(id: number): Observable<AuthUser[]> {
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/user/${id}/contacts`)
            .pipe(
                retry(3)
            );
    }

}
