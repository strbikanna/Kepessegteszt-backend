import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AppConstants} from "../../utils/constants";
import {Observable, retry} from "rxjs";
import {AuthUser} from "../../model/user/user-contacts.model";
import {UserRegistrationData} from "../../model/user/user_registration_data.model";
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
    searchUsersByName(name: string): Observable<AuthUser[]> | undefined {
        if (UserInfo.isAdmin()) {
            let params = new HttpParams();
            params = params.append('nameText', name);
            return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/search`, {params: params})
                .pipe(
                    retry(3)
                );
        }
        return undefined;
    }

    searchContactsByName(name: string): Observable<AuthUser[]> | undefined {
        let params = new HttpParams();
        params = params.append('nameText', name);
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/contacts/search`, {params: params})
            .pipe(
                retry(3)
            );
    }

    getAllUsers(pageNumber?: number, pageSize?: number): Observable<AuthUser[]> {
        let params = new HttpParams();
        if (pageNumber !== undefined && pageSize !== undefined) {
            params = params.append('pageNumber', pageNumber);
            params = params.append('pageSize', pageSize);
        }
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/all`, {params: params})
            .pipe(
                retry(3)
            );
    }

    getAllByNameStartsWith(name: string): Observable<AuthUser[]> {
        let params = new HttpParams().set('nameStartsWith', name);
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/all/nameStartsWith`, {params: params})
            .pipe(
                retry(3)
            );
    }

    getAllByUsernames(usernames: string[]): Observable<AuthUser[]> {
        let params = new HttpParams().set('usernames', usernames.join(','));
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/all`, {params: params})
            .pipe(
                retry(3)
            );
    }

    getContactsOfUser(user: AuthUser): Observable<AuthUser[]> {
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/${user.id}/contacts`)
            .pipe(
                retry(3)
            );
    }

    getContacts(): Observable<AuthUser[]> {
        return this.http.get<AuthUser[]>(`${AppConstants.authServerUrl}/${this.path}/impersonation_contacts`)
            .pipe(
                retry(3)
            );
    }

    getNumberOfUsers(): Observable<number> {
        return this.http.get<number>(`${AppConstants.authServerUrl}/${this.path}/count`)
            .pipe(
                retry(3)
            );
    }

    private path = `user`;

    updateUserData(user: AuthUser): Observable<AuthUser> {
        return this.http.put<AuthUser>(`${AppConstants.authServerUrl}/${this.path}/${user.id}`, user)
            .pipe(
                retry(3)
            );
    }

    registerUser(user: UserRegistrationData): Observable<AuthUser> {
        return this.http.post<AuthUser>(`${AppConstants.authServerUrl}/${this.path}/register_contact`, user)
            .pipe(
                retry(2)
            );
    }
}
