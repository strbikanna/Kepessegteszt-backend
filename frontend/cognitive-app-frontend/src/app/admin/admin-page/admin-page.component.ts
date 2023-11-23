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
        contactAutocompleteForm : new FormControl<UserForAdmin | string>(''),
    });


    contactOptions: UserForAdmin[] = [];
    filteredContactOptions: UserForAdmin[] = [];

    constructor(private service: AdminService, private _snackbar: MatSnackBar) {}

    ngOnInit(): void {
        this.users = this.service.getAllUsers(0, 10);
        this.service.getNumberOfUsers().subscribe(numberOfUsers => {
            this.dataLength = numberOfUsers;
        });
        this.initContactAutocomplete();
    }

    setUpUserToEdit(user: UserForAdmin): void {
        this.userToEdit = user;
        this.service.getContactsOfUser(user).subscribe(contacts => {
            this.userToEdit!!.contacts = contacts;
        });
        this.userDataForm.controls.firstName.setValue(user.firstName);
        this.userDataForm.controls.lastName.setValue(user.lastName);
        this.userDataForm.controls.username.setValue(user.username);
        this.userDataForm.controls.email.setValue(user.email);
        this.resetAutoComplete()
    }
    initContactAutocomplete(): void {
        console.log('init contact autocomplete')
        this.service.getAllUsers().subscribe(contacts => {
            this.contactOptions = contacts.sort((a, b) => a.firstName.localeCompare(b.firstName));
            this.filteredContactOptions = this.contactOptions.filter((val, index) => index < 10);
        });
        this.userDataForm.controls.contactAutocompleteForm.valueChanges.subscribe(value => {
            const name = typeof value === 'string' ? value : value?.firstName + ' ' + value?.lastName;
            this.filteredContactOptions = this.filterContacts(name);
        });
    }
    filterContacts(value: string) : UserForAdmin[]{
        if(value === '' || value === ' ') return this.contactOptions;
        const filter = value.toLowerCase();
        return this.contactOptions
            .filter(option => option.firstName.toLowerCase().includes(filter) || option.lastName.toLowerCase().includes(filter))
            .sort((a, b) => a.firstName.localeCompare(b.firstName))
            .filter((val, index) => index < 10);
    }
    convertDisplay(user: UserForAdmin): string{
        if(user.firstName === undefined || user.lastName === undefined) return '';
        return user.firstName + ' ' + user.lastName;
    }
    onAddContact(){
        if(this.userToEdit!!.contacts === undefined){
            this.userToEdit!!.contacts = [];
        }
        let contact: UserForAdmin;
        if(typeof this.userDataForm.controls.contactAutocompleteForm.value === 'string'){
           const possibleContact = this.contactOptions.find(contact => contact.firstName + ' ' + contact.lastName === this.userDataForm.controls.contactAutocompleteForm.value);
           if(possibleContact === undefined) return;
           contact = possibleContact;
        }else{
            if(this.userDataForm.controls.contactAutocompleteForm.value === null) return;
            contact = this.userDataForm.controls.contactAutocompleteForm.value;
        }
        if(this.userToEdit!!.contacts.find(existing => existing.id === contact.id) !== undefined) return;
        this.userToEdit!!.contacts.push(contact);
        this.resetAutoComplete();
    }

    resetAutoComplete(){
        this.userDataForm.controls.contactAutocompleteForm.setValue('');
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
        console.log(this.userToEdit)
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
    onRoleCheckChanged(role: string, checkedState: MatCheckboxChange){
        if(checkedState.checked){
            this.userToEdit?.roles.push(role);
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
