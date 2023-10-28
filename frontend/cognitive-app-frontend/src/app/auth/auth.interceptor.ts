import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import {Observable} from 'rxjs';
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

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if(this.isIgnorableRequest(request)){
      return next.handle(request);
    }
    if(this.isLoggedIn && !request.url.includes(this._ignoreTokenUrlPattern)){
      const requestWithToken = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + this.accessToken)
      });
      return next.handle(requestWithToken)
    }
    return next.handle(request);
  }

  isIgnorableRequest(request: HttpRequest<unknown>): boolean {
    return request.url.includes(this._ignoreTokenUrlPattern) ||
        (!request.url.includes(AppConstants.authServerUrl) && !request.url.includes(AppConstants.resourceServerUrl)) ||
        (request.method === 'POST' && request.url.includes("/gameplay"))
  }

}
