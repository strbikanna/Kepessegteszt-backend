import {Component, Inject} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-calculation-dialog',
  templateUrl: './calculation-dialog.component.html',
  styleUrls: ['./calculation-dialog.component.scss']
})
export class CalculationDialogComponent {
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
      },
  ) {
    if (data?.title) this.title = data.title;
    if (data?.message) this.message = data.message;
    if (data?.detail) this.detail = data.detail;
  }
}
