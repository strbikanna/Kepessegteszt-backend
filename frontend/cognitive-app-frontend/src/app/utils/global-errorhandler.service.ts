import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {AlertDialogComponent} from "../common/alert-dialog/alert-dialog.component";
import {TEXTS} from "./app.text_messages";
import {NavigationEnd, Router} from "@angular/router";
import {UserInfo} from "../auth/userInfo";

@Injectable({
    providedIn: 'root'
})
export class GlobalErrorhandlerService implements ErrorHandler {

    constructor(
        private zone: NgZone,
        private dialog: MatDialog,
        private router: Router) {
        router.events.subscribe((e) => {
            if (e instanceof NavigationEnd) {
                this.previousUrl = this.currentUrl
                this.currentUrl = e.url
            }
        })
    }

    private readonly httpError = TEXTS.error.http_error;
    private readonly httpErrorDetails = TEXTS.error.http_error_details;
    private currentUrl = ''
    private previousUrl = ''
    private previousError: any = undefined

    /**
     * Handles errors globally, displays to user with an alert dialog
     * @param error
     */
    handleError(error: any) {
        let message: string | undefined = undefined
        let detail: string | undefined = error.message
        // If the error is an HttpErrorResponse, extracts the error message and details
        if (error instanceof HttpErrorResponse) {
            message = this.httpError
            detail = detail ?? this.httpErrorDetails
            if (error.status === 401) {
                UserInfo.loginStatus.next(false)
                this.zone.run(() =>
                    this.router.navigate(['/'])
                )
            }
            if (error.status === 403) {
                this.router.navigate([this.previousUrl])
            }
        } else {
            //If error occurs multiple times, only show it once
            if (error && this.previousError && typeof error === typeof this.previousError && error.message === this.previousError.message) {
                console.log('Same error multiple times')
                return
            }
        }
        // Opens the alert dialog with error message and details
        this.zone.run(() =>
            this.dialog.open(
                AlertDialogComponent,
                {
                    data: {message: message, detail: detail}
                }
            )
        );
        this.previousError = error ?? this.previousError
        console.error('Error from global error handler', error);
    }
}
