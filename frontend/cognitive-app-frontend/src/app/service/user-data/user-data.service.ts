import {Injectable} from '@angular/core';
import {SimpleHttpService} from "../../utils/simple-http.service";
import {HttpClient} from "@angular/common/http";
import {User} from "../../model/user.model";
import {map, Observable} from "rxjs";
import {AppConstants} from "../../utils/constants";
import {AuthUser} from "../../model/user-contacts.model";

@Injectable({
    providedIn: 'root'
})
export class UserDataService {
    private path = '/user'
    private authServerUrl = AppConstants.authServerUrl

    constructor(private http: HttpClient, private httpService: SimpleHttpService) {
    }

    getUserData(): Observable<User> {
        return this.http.get<User>(this.httpService.baseUrl + this.path + '/personal_data').pipe(
            map(user => {
                const year = user.birthDate?.toString()?.substring(6)
                const month = user.birthDate?.toString()?.substring(3, 5)
                const day = user.birthDate?.toString()?.substring(0, 2)
                if (user.birthDate) user.birthDate = new Date( Date.UTC(Number(year), Number(month)-1, Number(day)))
                return user
            }));
    }

    updateUserData(user: User): Observable<User> {
        const birthDate = user.birthDate?.toISOString()
        const userToSend = {
            firstName: user.firstName,
            lastName: user.lastName,
            username: user.username,
            birthDate: birthDate,
            address: user.address,
            gender: user.gender
        }
        return this.http.put<User>(this.httpService.baseUrl + this.path + '/personal_data', userToSend)
    }

    updateUserDataAuthServer(user: AuthUser): Observable<AuthUser> {
        return this.http.put<AuthUser>(this.authServerUrl + this.path + `/personal_data/${user.username}`, user)
    }
}
