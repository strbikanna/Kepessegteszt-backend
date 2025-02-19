import {Component, Inject} from '@angular/core';
import {TEXTS} from "../../../../text/app.text_messages";
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {Organization} from "../../../../model/user-group";

@Component({
  selector: 'app-create-org-dialog',
  templateUrl: './create-org-dialog.component.html',
  styleUrls: ['./create-org-dialog.component.scss']
})
export class CreateOrgDialogComponent {
  text = TEXTS.group_management
  actionText = TEXTS.actions
  orgForm = this.fb.group({
    name: ['', Validators.required],
    addressCity: ['', Validators.required],
    addressZip: ['', Validators.required],
    addressStreet: ['', Validators.required],
    addressHouseNumber: ['', Validators.required],
  });

  constructor(
      @Inject(MAT_DIALOG_DATA)
      protected data: {
        parentGroupName: string;
        organization: Organization;
        onOk: (org: any) => void;
        onCancel: () => void;
      },
      private fb: FormBuilder
  ) { }

  createOrg(){
    if(this.orgForm.valid){
      const org = {
        name: this.orgForm.get('name')?.value ?? '',
        address: {
          city: this.orgForm.get('addressCity')?.value ?? '',
          zip: this.orgForm.get('addressZip')?.value ?? '',
          street: this.orgForm.get('addressStreet')?.value ?? '',
          houseNumber: this.orgForm.get('addressHouseNumber')?.value ?? '',
        }
      }
      this.data.onOk(org)
    }
  }
}
