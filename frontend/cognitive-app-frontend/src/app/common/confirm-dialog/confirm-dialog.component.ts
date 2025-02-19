import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TEXTS} from "../../text/app.text_messages";

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {

  protected readonly texts = TEXTS.actions
  constructor(
      @Inject(MAT_DIALOG_DATA)
      protected data: {
        title: string;
        message: string;
        onOk: () => void;
        onCancel: () => void;
      },
  ) { }
}
