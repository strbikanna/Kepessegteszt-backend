import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {AlertDialogComponent} from "../common/alert-dialog/alert-dialog.component";
import {TEXTS} from "./app.text_messages";

@Injectable({
    providedIn: 'root'
})
export class GlobalErrorhandlerService implements ErrorHandler {

    constructor(
        private zone: NgZone,
        private dialog: MatDialog
    ) {}
    private readonly httpError = TEXTS.error.http_error;
    private readonly httpErrorDetails = TEXTS.error.http_error_details;

    handleError(error: any) {
        let message : string | undefined = undefined
        let detail : string | undefined = error.message
        if(error instanceof HttpErrorResponse){
            message = this.httpError
            detail = detail ?? this.httpErrorDetails
        }
        this.zone.run(() =>
            this.dialog.open(
                AlertDialogComponent,
                {
                    data: {message: message, detail: detail }
                }
            )
        );

        console.error('Error from global error handler', error);
    }
}
