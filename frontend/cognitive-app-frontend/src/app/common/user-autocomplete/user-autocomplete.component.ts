import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {AuthUser} from "../../model/user-contacts.model";
import {FormControl} from "@angular/forms";
import {ContactService} from "../../service/contact_service/contact.service";
import {Observable} from "rxjs";
import {AdminService} from "../../service/admin/admin.service";
import {UserFilter} from "../user-filter/user-filter.model";
import {UserInfo} from "../../auth/userInfo";

@Component({
    selector: 'app-user-autocomplete',
    templateUrl: './user-autocomplete.component.html',
    styleUrls: ['./user-autocomplete.component.scss']
})
export class UserAutocompleteComponent implements OnInit {
    @Input() text = TEXTS.user_autocomplete;
    @Input() multiple = true;
    @Input() searchOnlyContacts = true;
    @Input() selectedUserNames: string[] = [];
    /**
     * emits the actually selected users
     */
    @Output() userSelectionChanged: EventEmitter<AuthUser[]> = new EventEmitter<AuthUser[]>();
    @Output() userSelected: EventEmitter<AuthUser> = new EventEmitter<AuthUser>();

    protected userOptions: AuthUser[] = [];
    protected defaultUserOptions: AuthUser[] = [];
    protected selectedUsers: AuthUser[] = [];
    autocompleteForm = new FormControl<AuthUser | string>('')

    constructor(private service: AdminService) {
    }

    ngOnInit() {
        if (this.searchOnlyContacts) {
            this.service.getContacts().subscribe(users => {
                this.userOptions = users;
                this.defaultUserOptions = users;
            });
        } else {
            this.service.getAllUsers(0, 30).subscribe(users => {
                this.userOptions = users;
                this.defaultUserOptions = users;
            });
        }
        this.selectedUserNames = this.selectedUserNames.filter(name => name && name.trim() !== '');
        if(this.selectedUserNames.length > 0){
            console.log(this.selectedUserNames);
            this.loadUsersByName(this.selectedUserNames);
        }
        this.autocompleteForm.valueChanges.subscribe(value => {
            const name = typeof value === 'string' ? value : value?.firstName + ' ' + value?.lastName;
            if (name.length > 2) {
                this.searchUsers(name);
            } else if (name.length === 0) {
                this.userOptions = this.defaultUserOptions;
            } else {
                this.userOptions = this.userOptions
                    .filter(user => user.firstName.toLowerCase().includes(name.toLowerCase()) || user.lastName.toLowerCase().includes(name.toLowerCase()))
                    .sort((a, b) => a.firstName.localeCompare(b.firstName));
            }
        });
    }

    searchUsers(value: string) {
        if (value === '' || value === ' ') return;
        const filter = value.toLowerCase();
        if(this.searchOnlyContacts){
            this.service.searchContactsByName(filter)?.subscribe(users => {
                this.userOptions = users;
            });
        }else{
            this.service.searchUsersByName(filter)?.subscribe(users => {
                this.userOptions = users;
            });
        }
    }

    resetAutoComplete() {
        this.autocompleteForm.setValue('');
        this.userOptions = this.defaultUserOptions;
    }

    onUserSelected() {
        let user: AuthUser;
        if (typeof this.autocompleteForm.value === 'string') {
            const selectedUser = this.userOptions.find(contact => contact.firstName + ' ' + contact.lastName === this.autocompleteForm.value);
            if (selectedUser === undefined) return;
            user = selectedUser;
        } else {
            if (this.autocompleteForm.value === null) return;
            user = this.autocompleteForm.value;
        }
        if (!this.multiple) {
            this.selectedUsers = [user];
            this.userSelected.emit(user);
            return;
        }
        if (!this.selectedUsers.includes(user)) {
            this.selectedUsers.push(user);
            this.userSelectionChanged.emit(this.selectedUsers);
        }
        this.resetAutoComplete();
    }

    convertDisplay(user: AuthUser): string {
        if (user.firstName === undefined || user.lastName === undefined) return '';
        return user.firstName + ' ' + user.lastName;
    }

    onUserRemoved(user: AuthUser) {
        this.selectedUsers = this.selectedUsers.filter(u => u !== user);
        this.userSelectionChanged.emit(this.selectedUsers);
        this.autocompleteForm.setValue('');
    }

    private loadUsersByName(usersNames: string[]) {
        this.selectedUsers = [];
        usersNames.forEach(name => {
            if(this.searchOnlyContacts){
                this.service.searchContactsByName(name)?.subscribe(users => {
                    this.selectedUsers.push(...users)
                });
            }else{
                this.service.searchUsersByName(name)?.subscribe(users => {
                    this.selectedUsers.push(...users)
                });
            }
        });

    }
}
