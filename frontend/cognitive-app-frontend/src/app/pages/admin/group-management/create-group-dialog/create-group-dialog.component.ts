import {Component, Inject} from '@angular/core';
import {TEXTS} from "../../../../text/app.text_messages";
import {User} from "../../../../model/user.model";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {Organization} from "../../../../model/user-group";

@Component({
  selector: 'app-create-group-dialog',
  templateUrl: './create-group-dialog.component.html',
  styleUrls: ['./create-group-dialog.component.scss']
})
export class CreateGroupDialogComponent {
  text = TEXTS.group_management
  actionText = TEXTS.actions
  groupForm = new FormControl('', Validators.required)
  constructor(
      @Inject(MAT_DIALOG_DATA)
      protected data: {
        parentGroupName: string;
        organization: Organization;
        onOk: (group: any) => void;
        onCancel: () => void;
      },
  ) { }

  createGroup(){
    if(this.groupForm.valid){
      const group = {
        name: this.groupForm.value ?? '',
        organization: this.data.organization,
      }
      this.data.onOk(group)
    }
  }

}
