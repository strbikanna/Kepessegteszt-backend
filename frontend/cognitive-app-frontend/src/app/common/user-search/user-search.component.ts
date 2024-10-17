import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ContactService} from "../../service/contact_service/contact.service";
import {UserForAdmin} from "../../model/user-contacts.model";
import {FormControl} from "@angular/forms";
import {TEXTS} from "../../utils/app.text_messages";

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.scss']
})
export class UserSearchComponent implements OnInit {

  @Input() text = TEXTS.contact_autocomplete;
  @Input() displayCurrentUser: boolean = false;
  @Input() showOnlyContacts: boolean = false;
  @Input() userToExclude: UserForAdmin | undefined
  @Output() onContactSelected: EventEmitter<UserForAdmin> = new EventEmitter<UserForAdmin>();

  protected contactOptions: UserForAdmin[] = [];
  protected filteredContactOptions: UserForAdmin[] = [];
  contactAutocompleteForm = new FormControl<UserForAdmin | string>('')

  constructor(private service: ContactService) { }

  ngOnInit(): void {
    this.service.getCurrUser().subscribe(user => {
      this.service.getContactsToShow(user, this.showOnlyContacts).subscribe(contacts => {
        this.contactOptions = contacts.sort((a, b) => a.firstName.localeCompare(b.firstName));
        if(!this.displayCurrentUser) this.contactOptions = this.contactOptions.filter(contact => contact.id !== user.id);
        if(this.userToExclude) this.contactOptions = this.contactOptions.filter(contact => contact.id !== this.userToExclude?.id)
        this.filteredContactOptions = this.contactOptions.filter((val, index) => index < 10);
      });
    });
    this.contactAutocompleteForm.valueChanges.subscribe(value => {
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
  resetAutoComplete(){
    this.contactAutocompleteForm.setValue('');
  }
  onAddContact(){
    let contact: UserForAdmin;
    if(typeof this.contactAutocompleteForm.value === 'string'){
      const possibleContact = this.contactOptions.find(contact => contact.firstName + ' ' + contact.lastName === this.contactAutocompleteForm.value);
      if(possibleContact === undefined) return;
      contact = possibleContact;
    }else{
      if(this.contactAutocompleteForm.value === null) return;
      contact = this.contactAutocompleteForm.value;
    }
    this.onContactSelected.emit(contact);
    this.resetAutoComplete();
  }
  convertDisplay(user: UserForAdmin): string{
    if(user.firstName === undefined || user.lastName === undefined) return '';
    return user.firstName + ' ' + user.lastName;
  }

}
