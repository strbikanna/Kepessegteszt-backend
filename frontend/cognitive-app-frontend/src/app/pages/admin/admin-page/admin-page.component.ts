import {Component, OnInit} from '@angular/core';
import {AdminService} from "../../../service/admin/admin.service";
import {map, Observable} from "rxjs";
import {AuthUser} from "../../../model/user/user-contacts.model";
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatPaginatorIntl, PageEvent, MatPaginator } from "@angular/material/paginator";
import {Role} from "../../../utils/constants";
import { MatCheckboxChange, MatCheckbox } from "@angular/material/checkbox";
import {TEXTS} from "../../../text/app.text_messages";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PaginatorTranslator} from "../../../common/paginator/paginator-translator";
import { HorizontalScrollerComponent } from './horizontal-scroller/horizontal-scroller.component';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatChip } from '@angular/material/chips';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { UserSearchComponent } from '../../../common/user-search/user-search.component';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'app-admin-page',
    templateUrl: './admin-page.component.html',
    styleUrls: ['./admin-page.component.scss'],
    providers: [{ provide: MatPaginatorIntl, useClass: PaginatorTranslator }],
    standalone: true,
    imports: [
        MatPaginator,
        HorizontalScrollerComponent,
        NgFor,
        NgIf,
        MatButton,
        MatChip,
        MatIcon,
        ReactiveFormsModule,
        MatFormField,
        MatLabel,
        MatInput,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        MatExpansionPanelTitle,
        MatCheckbox,
        UserSearchComponent,
        MatIconButton,
        MatDivider,
        AsyncPipe,
    ],
})
export class AdminPageComponent implements OnInit {

    pageSizeOptions = [10, 25, 100];
    dataLength = 0;
    defaultPageSize = 10;
    lastPageEvent: PageEvent | undefined = undefined;

    text = TEXTS.admin_page;
    nameStartLetters = ['A', 'Á', 'B', 'C', 'Cs', 'D', 'E', 'É', 'F',  'G', 'Gy', 'H', 'I', 'Í', 'J', 'K', 'L', 'M', 'N', 'Ny', 'O', 'Ó', 'Ö', 'Ő', 'P', 'Q', 'R', 'S', 'Sz', 'T', 'Ty', 'U', 'Ú', 'Ü', 'Ű', 'V', 'W', 'X', 'Y', 'Z', 'Zs'];

    users: Observable<AuthUser[]> = new Observable<AuthUser[]>();

    userToEdit: AuthUser | undefined = undefined;

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
        this.initUsers()
    }

    initUsers(): void {
        this.users = this.service.getAllUsers(0, 10);
        this.service.getNumberOfUsers().subscribe(numberOfUsers => {
            this.dataLength = numberOfUsers;
        });
    }

    /**
     * When user chosen, fill userdataForm with user data
     * @param user chosen user
     */
    setUpUserToEdit(user: AuthUser): void {
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
    onAddContact(contact: AuthUser): void {
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

    hasRequestedRole(user: AuthUser) {
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
    notRolesOfUser(user: AuthUser): string[] {
        let allRoles = [Role.ADMIN, Role.PARENT, Role.TEACHER, Role.STUDENT, Role.SCIENTIST];
        return allRoles.filter(role => !user.roles.includes(role));
    }
    actualRolesOfUser(user: AuthUser): string[] {
        return user.roles.filter(role => !role.includes('REQUEST'));
    }
    requestedRolesOfUser(user: AuthUser): string[] {
        return user.roles.filter(role => role.includes('REQUEST'));
    }

    /**
     * When checkbox of role is changed, add or remove role from userToEdit, and handle requested role grant
     * @param role
     * @param checkedState
     */
    onRoleCheckChanged(role: string, checkedState: MatCheckboxChange){
        let userRole = role.toUpperCase() as Role;
        if(checkedState.checked){
            this.userToEdit?.roles.push(userRole);
            if(this.userToEdit?.roles.includes(`${role}_REQUEST`.toUpperCase() as Role)){
                this.userToEdit?.roles.splice(this.userToEdit?.roles.indexOf(`${role}_REQUEST`.toUpperCase() as Role), 1);
            }
        }else{
            this.userToEdit?.roles.splice(this.userToEdit?.roles.indexOf(userRole), 1);
        }
    }
    onContactDeleted(contact: AuthUser){
        this.userToEdit?.contacts?.splice(this.userToEdit?.contacts?.indexOf(contact), 1);
    }

    searchByNameStartsWith(name: string){
        this.users = this.service.getAllByNameStartsWith(name)

    }
    private showSuccessSnackbar() {
        this._snackbar.open(this.text.update_success_message, this.text.actions.ok,{duration: 5 * 1000} )
    }

    protected readonly Role = Role;
}
