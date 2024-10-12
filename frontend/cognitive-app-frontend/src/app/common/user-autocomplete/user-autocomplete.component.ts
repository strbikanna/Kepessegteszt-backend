import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {UserForAdmin} from "../../admin/model/user-contacts.model";
import {FormControl} from "@angular/forms";
import {ContactService} from "../../admin/common/user_contact_autocomplete/contact_service/contact.service";
import {Observable} from "rxjs";
import {AdminService} from "../../admin/service/admin.service";

@Component({
  selector: 'app-user-autocomplete',
  templateUrl: './user-autocomplete.component.html',
  styleUrls: ['./user-autocomplete.component.scss']
})
export class UserAutocompleteComponent implements OnInit{
  @Input() text = TEXTS.contact_autocomplete;
  /**
   * emits the actually selected users username
   */
  @Output() onUserSelectionChanged: EventEmitter<string[]> = new EventEmitter<string[]>();

  protected userOptions: UserForAdmin[] = [];
  protected defaultUserOptions: UserForAdmin[] = [];
  protected selectedUsers: UserForAdmin[] = [];
  autocompleteForm = new FormControl<UserForAdmin | string>('')

  constructor(private service: AdminService) { }

  ngOnInit() {
    this.service.getAllUsers(0, 30).subscribe(users => {
        this.userOptions = users;
        this.defaultUserOptions = users;
    });
    this.autocompleteForm.valueChanges.subscribe(value => {
      const name = typeof value === 'string' ? value : value?.firstName + ' ' + value?.lastName;
      if(name.length > 2){
        this.searchUsers(name);
      }
      if(name.length === 0){
        this.userOptions = this.defaultUserOptions;
      }
    });
  }
  searchUsers(value: string){
    if(value === '' || value === ' ') return;
    const filter = value.toLowerCase();
    this.service.searchUsersByName(filter)?.subscribe(users => {
        this.userOptions = users;
    });
  }
  resetAutoComplete(){
    this.autocompleteForm.setValue('');
    this.userOptions = this.defaultUserOptions;
  }
  onUserSelected(){
    let user: UserForAdmin;
    if(typeof this.autocompleteForm.value === 'string'){
      const selectedUser = this.userOptions.find(contact => contact.firstName + ' ' + contact.lastName === this.autocompleteForm.value);
      if(selectedUser === undefined) return;
      user = selectedUser;
    }else{
      if(this.autocompleteForm.value === null) return;
      user = this.autocompleteForm.value;
    }
    if(!this.selectedUsers.includes(user)) {
      this.selectedUsers.push(user);
      this.onUserSelectionChanged.emit(this.selectedUsers.map(u => u.username));
    }
    this.resetAutoComplete();
  }
  convertDisplay(user: UserForAdmin): string{
    if(user.firstName === undefined || user.lastName === undefined) return '';
    return user.firstName + ' ' + user.lastName;
  }
  onUserRemoved(user: UserForAdmin){
    this.selectedUsers = this.selectedUsers.filter(u => u !== user);
    this.onUserSelectionChanged.emit(this.selectedUsers.map(u => u.username));
  }
}
