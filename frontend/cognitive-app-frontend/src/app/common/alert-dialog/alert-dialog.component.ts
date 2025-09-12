import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TEXTS} from "../../text/app.text_messages";

/**
 * Dialog component to display custom text
 */
@Component({
  selector: 'app-alert-dialog',
  templateUrl: './alert-dialog.component.html',
  styleUrls: ['./alert-dialog.component.scss']
})
export class AlertDialogComponent {
  title: string = TEXTS.error.default_error_title
  message: string = TEXTS.error.default_error_message
  detail: string = ''
  buttonText = TEXTS.actions.ok

  constructor(
      @Inject(MAT_DIALOG_DATA)
      private data: {
        title: string;
        message: string;
        detail: string;
        onOk?: () => void;
      },
  ) {
    if (data?.title) this.title = data.title;
    if (data?.message) this.message = data.message;
    if (data?.detail) this.detail = data.detail;
  }

  onOk() {
    this.data.onOk?.()
  }
}
