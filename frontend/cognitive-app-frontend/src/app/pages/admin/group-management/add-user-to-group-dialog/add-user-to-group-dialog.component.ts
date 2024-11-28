import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {User} from "../../../../model/user.model";
import {TEXTS} from "../../../../utils/app.text_messages";

@Component({
  selector: 'app-add-user-to-group-dialog',
  templateUrl: './add-user-to-group-dialog.component.html',
  styleUrls: ['./add-user-to-group-dialog.component.scss']
})
export class AddUserToGroupDialogComponent {
  text = TEXTS.actions
  selectedUser?: User
  constructor(
      @Inject(MAT_DIALOG_DATA)
      protected data: {
        title: string;
        onOk: (user: User) => void;
        onCancel: () => void;
      },
  ) { }

  onUserSelected(user: User){
    this.selectedUser = user
  }

  onSubmit(){
    if(this.selectedUser){
      this.data.onOk(this.selectedUser)
    }
  }

    onCancel(){
        this.data.onCancel()
    }
}
