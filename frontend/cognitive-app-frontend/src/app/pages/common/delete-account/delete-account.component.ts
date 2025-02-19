import {Component, inject} from '@angular/core';
import {FormBuilder, FormControl} from "@angular/forms";
import {UserDataService} from "../../../service/user-data/user-data.service";
import {LoginService} from "../../../general/login/login.service";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../../common/confirm-dialog/confirm-dialog.component";
import {TEXTS} from "../../../text/app.text_messages";
import {AlertDialogComponent} from "../../../common/alert-dialog/alert-dialog.component";

@Component({
  selector: 'app-delete-account',
  templateUrl: './delete-account.component.html',
  styleUrls: ['./delete-account.component.scss']
})
export class DeleteAccountComponent {
  text = TEXTS.user_info
  constructor(private service: UserDataService, private loginService: LoginService, private router: Router) {}
  readonly dialog = inject(MatDialog);
  deleteAccountControl: FormControl = new FormControl(false);

  deleteAccount() {
    this.service.deleteUserForever().subscribe((a) => {
      console.log('Account deleted', a);
      this.openAccountDeletedDialog()
    });
  }

  private openAccountDeletedDialog() {
    this.dialog.open(AlertDialogComponent, {
      data: {
        title: this.text.account_deleted,
        message: this.text.account_deleted_message,
        onOk: () => {
          this.loginService.logout()
          this.router.navigate(['/'])
        },
      },
    });
  }
}
