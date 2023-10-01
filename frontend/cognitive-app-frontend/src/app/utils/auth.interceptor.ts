import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserInfo} from "./userInfo";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private accessToken = ""
  private isLoggedIn = false
  private readonly _actAsParamName = 'act_as';

  constructor() {
    UserInfo.loginStatus.subscribe(loginSuccess => {
      this.isLoggedIn = loginSuccess
      this.accessToken = UserInfo.accessToken
    });
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if(this.isLoggedIn) {
      const requestWithToken = request.clone({
        headers: request.headers.set('Authorization', 'Bearer ' + this.accessToken)
      });
      return next.handle(requestWithToken)
    }
    return next.handle(request);
  }

}
