import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {AppConstants} from "./constants";
import {throwError} from "rxjs";
import {TEXTS} from "./app.text_messages";

@Injectable({
  providedIn: 'root'
})
export class SimpleHttpService {
  readonly baseUrl = AppConstants.resourceServerUrl;

  /**
   * Handle network errors: give reason to status code
   * @param error
   */
  handleHttpError(error: HttpErrorResponse) {
    if (error.status === 0) {
      // A client-side or network error occurred.
      return throwError(() => new Error(TEXTS.error.network_error));
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      if(error.status === 401 || error.status === 403){
        return throwError(() => new Error(TEXTS.error.unauthorized_error));
      }
      if(error.status === 500){
        return throwError(() => new Error(error.message));
      }
    }
    return throwError(() => error);
  }
}
