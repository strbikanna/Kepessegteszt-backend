import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {AlertDialogComponent} from "../common/alert-dialog/alert-dialog.component";
import {TEXTS} from "./app.text_messages";
import {NavigationEnd, Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class GlobalErrorhandlerService implements ErrorHandler {

    constructor(
        private zone: NgZone,
        private dialog: MatDialog,
        private router: Router
    ) {
        router.events.subscribe((e) =>{
            if(e instanceof NavigationEnd){
                this.previousUrl = this.currentUrl
                this.currentUrl = e.url
            }
        })
    }
    private readonly httpError = TEXTS.error.http_error;
    private readonly httpErrorDetails = TEXTS.error.http_error_details;
    private currentUrl = ''
    private previousUrl = ''
    private previousError : any = undefined

    handleError(error: any) {
        let message : string | undefined = undefined
        let detail : string | undefined = error.message
        if(error instanceof HttpErrorResponse){
            message = this.httpError
            detail = detail ?? this.httpErrorDetails
            if(error.status === 401){
                this.router.navigate(['/'])
            }
            if(error.status === 403){
                this.router.navigate([this.previousUrl])
            }
        }else{
            if(error && this.previousError && typeof error === typeof this.previousError && error.message === this.previousError.message){
                console.log('Same error multiple times')
                return
            }
        }
        this.zone.run(() =>
            this.dialog.open(
                AlertDialogComponent,
                {
                    data: {message: message, detail: detail }
                }
            )
        );
        this.previousError = error ?? this.previousError
        console.error('Error from global error handler', error);
    }
}
