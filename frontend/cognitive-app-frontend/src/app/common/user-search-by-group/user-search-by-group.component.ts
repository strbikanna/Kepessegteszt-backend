import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserGroupSearchService} from "../../service/user-group/user-group-search.service";
import {UserGroup} from "../../model/user_group.model";
import {User} from "../../model/user.model";
import {FormControl} from "@angular/forms";
import {TEXTS} from "../../utils/app.text_messages";

@Component({
  selector: 'app-user-search-by-group',
  templateUrl: './user-search-by-group.component.html',
  styleUrls: ['./user-search-by-group.component.scss']
})
export class UserSearchByGroupComponent implements  OnInit {
  @Output() userSelected: EventEmitter<User> = new EventEmitter<User>()

  allGroupIds: number[] = []
  userOptions: User[] = []

  searchNameControl = new FormControl('')

  text = TEXTS.user_autocomplete

  constructor(private service: UserGroupSearchService) {
  }

  ngOnInit() {
    this.loadUserGroups();
    this.searchNameControl.valueChanges.subscribe(() => this.onSearchNameChange())
    this.service.getAllUsersToSee().subscribe(users => {
        this.userOptions = users
    });
  }

  onSearchNameChange(){
    const searchName = this.searchNameControl.value
    if(!searchName)return;
    if(searchName.length > 2){
      this.searchUsers(searchName)
    }else{
      this.userOptions = this.userOptions.filter(user => user.firstName.startsWith(searchName) || user.lastName.startsWith(searchName))
    }
  }

  onOptionSelected(){
    const name = this.searchNameControl.value
    const user = this.userOptions.find(user => name === `${user.firstName} ${user.lastName}`)
    if(user){
      this.userSelected.emit(user)
    }
  }

  private loadUserGroups(){
    this.service.getAllUserGroups().subscribe(groups => {
      this.allGroupIds = groups.map(group => group.id)
    });
  }

  private searchUsers(name: string){
    this.service.searchMembers(this.allGroupIds, name).subscribe(users => {
      this.userOptions = users
    });

  }

}
