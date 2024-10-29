import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse,} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {UserInfo} from "./userInfo";
import {AppConstants} from "../utils/constants";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    private accessToken = ""
    private isLoggedIn = false
    private readonly _ignoreTokenUrlPattern = '/token';

    constructor() {
        UserInfo.loginStatus.subscribe(loginSuccess => {
            this.isLoggedIn = loginSuccess
            this.accessToken = UserInfo.accessToken
        });
    }

    /**
     * Intercept all requests and add bearer token if needed
     * @param request
     * @param next
     */
    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        //ignore requests that don't need token
        if (this.isIgnorableRequest(request)) {
            return next.handle(request)
        }
        //catch returned token
        if (this.isTokenRequest(request)) {
            return next.handle(request)
                .pipe(
                    tap(response => {
                        if (response instanceof HttpResponse) {
                            const responseBody = response.body as any
                            if(responseBody.scope === 'openid') {
                                this.accessToken = (response.body as any).access_token ?? (response.body as any).accessToken
                                console.log("Access token refreshed ")
                            }
                        }
                    })
                )
        }
        //add bearer token to request
        if (this.isLoggedIn && !request.url.includes(this._ignoreTokenUrlPattern)) {
            const requestWithToken = request.clone({
                headers: request.headers.set('Authorization', 'Bearer ' + this.accessToken)
            });
            return next.handle(requestWithToken)
        }
        return next.handle(request);
    }

    private isIgnorableRequest(request: HttpRequest<unknown>): boolean {
        return (!request.url.includes(AppConstants.authServerUrl) && !request.url.includes(AppConstants.resourceServerUrl)) ||
            (request.method === 'POST' && request.url.includes("/gameplay"))
    }
    private isTokenRequest(request: HttpRequest<unknown>): boolean {
        return request.url.includes(AppConstants.authServerUrl) && request.method === 'POST' && request.url.includes(this._ignoreTokenUrlPattern)
    }

}
