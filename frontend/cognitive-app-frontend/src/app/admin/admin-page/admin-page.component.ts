import {Component, OnInit} from '@angular/core';
import {AdminService} from "../service/admin.service";
import {map, Observable} from "rxjs";
import {UserForAdmin} from "../model/user-contacts.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MatPaginatorIntl, PageEvent} from "@angular/material/paginator";
import {Role} from "../../utils/constants";
import {MatCheckboxChange} from "@angular/material/checkbox";
import {TEXTS} from "../../utils/app.text_messages";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PaginatorTranslator} from "../../common/paginator/paginator-translator";

@Component({
    selector: 'app-admin-page',
    templateUrl: './admin-page.component.html',
    styleUrls: ['./admin-page.component.scss'],
    providers: [{provide: MatPaginatorIntl, useClass: PaginatorTranslator}],
})
export class AdminPageComponent implements OnInit {

    pageSizeOptions = [10, 25, 100];
    dataLength = 0;
    defaultPageSize = 10;
    lastPageEvent: PageEvent | undefined = undefined;

    text = TEXTS.admin_page;

    users: Observable<UserForAdmin[]> = new Observable<UserForAdmin[]>();

    userToEdit: UserForAdmin | undefined = undefined;

    userDataForm = new FormGroup({
        firstName: new FormControl<string>( '', Validators.required ),
        lastName: new FormControl<string>('', Validators.required),
        username: new FormControl<string>({value: '', disabled: true}),
        email: new FormControl<string>('', [Validators.required, Validators.email]),
    });

    constructor(private service: AdminService, private _snackbar: MatSnackBar) {}

    /**
     * Init paged user data
     */
    ngOnInit(): void {
        this.users = this.service.getAllUsers(0, 10);
        this.service.getNumberOfUsers().subscribe(numberOfUsers => {
            this.dataLength = numberOfUsers;
        });
    }

    /**
     * When user chosen, fill userdataForm with user data
     * @param user chosen user
     */
    setUpUserToEdit(user: UserForAdmin): void {
        this.userToEdit = user;
        this.service.getContactsOfUser(user).subscribe(contacts => {
            this.userToEdit!!.contacts = contacts;
        });
        this.userDataForm.controls.firstName.setValue(user.firstName);
        this.userDataForm.controls.lastName.setValue(user.lastName);
        this.userDataForm.controls.username.setValue(user.username);
        this.userDataForm.controls.email.setValue(user.email);
    }
    /**
     * Adds contact to userToEdit
     */
    onAddContact(contact: UserForAdmin): void {
        if(this.userToEdit!!.contacts === undefined){
            this.userToEdit!!.contacts = [];
        }
        if(this.userToEdit!!.contacts.find(existing => existing.id === contact.id) !== undefined) return;
        this.userToEdit!!.contacts.push(contact);
    }

    handlePageEvent(event: PageEvent): void {
        this.lastPageEvent = event;
        this.defaultPageSize = event.pageSize;
        this.users = this.service.getAllUsers(event.pageIndex, event.pageSize);
    }

    hasRequestedRole(user: UserForAdmin) {
        return user.roles.some(role => role.includes('REQUEST'));
    }

    saveChangesOfUserToEdit(): void {
        if (this.userToEdit === undefined) return
        this.userToEdit.firstName = this.userDataForm.controls.firstName.value!!
        this.userToEdit.lastName = this.userDataForm.controls.lastName.value!!
        this.userToEdit.username = this.userDataForm.controls.username.value!!
        this.userToEdit.email = this.userDataForm.controls.email.value!!
        this.service.updateUserData(this.userToEdit).subscribe(updatedUser => {
            this.showSuccessSnackbar()
            this.users.pipe(
                map(users => users.map(user => {
                    if(user.id === updatedUser.id) return updatedUser;
                    else return user;
                })
                )
            )
            this.users = this.service.getAllUsers(this.lastPageEvent?.pageIndex ?? 0, this.lastPageEvent?.pageSize ?? this.defaultPageSize);
        });
        this.userToEdit = undefined;
    }
    notRolesOfUser(user: UserForAdmin): string[] {
        let allRoles = [Role.ADMIN, Role.PARENT, Role.TEACHER, Role.STUDENT, Role.SCIENTIST];
        return allRoles.filter(role => !user.roles.includes(role));
    }
    actualRolesOfUser(user: UserForAdmin): string[] {
        return user.roles.filter(role => !role.includes('REQUEST'));
    }
    requestedRolesOfUser(user: UserForAdmin): string[] {
        return user.roles.filter(role => role.includes('REQUEST'));
    }

    /**
     * When checkbox of role is changed, add or remove role from userToEdit, and handle requested role grant
     * @param role
     * @param checkedState
     */
    onRoleCheckChanged(role: string, checkedState: MatCheckboxChange){
        role = role.toUpperCase();
        if(checkedState.checked){
            this.userToEdit?.roles.push(role);
            if(this.userToEdit?.roles.includes(role + '_REQUEST')){
                this.userToEdit?.roles.splice(this.userToEdit?.roles.indexOf(role + '_REQUEST'), 1);
            }
        }else{
            this.userToEdit?.roles.splice(this.userToEdit?.roles.indexOf(role), 1);
        }
    }
    onContactDeleted(contact: UserForAdmin){
        this.userToEdit?.contacts?.splice(this.userToEdit?.contacts?.indexOf(contact), 1);
    }
    private showSuccessSnackbar() {
        this._snackbar.open(this.text.update_success_message, this.text.actions.ok,{duration: 5 * 1000} )
    }

}
