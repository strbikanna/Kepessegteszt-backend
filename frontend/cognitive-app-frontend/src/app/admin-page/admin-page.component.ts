import {Component, OnInit} from '@angular/core';
import {AdminService} from "./service/admin.service";
import {Observable} from "rxjs";
import {UserForAdmin} from "./model/user-contacts.model";
import {FormControl, FormGroup} from "@angular/forms";
import {PageEvent} from "@angular/material/paginator";

@Component({
    selector: 'app-admin-page',
    templateUrl: './admin-page.component.html',
    styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {

    pageSizeOptions= [10, 25, 100];
    dataLength = 0;
    defaultPageSize = 10;

    users: Observable<UserForAdmin[]> = new Observable<UserForAdmin[]>();

    userToEdit: UserForAdmin | undefined = undefined;

    userDataForm = new FormGroup({
        firstName: new FormControl<string>(this.userToEdit?.firstName ?? ''),
        lastName: new FormControl<string>(this.userToEdit?.lastName ?? ''),
        username: new FormControl<string>(this.userToEdit?.lastName ?? ''),
        roles: new FormControl<string[]>(this.userToEdit?.roles ?? []),
        contacts: new FormControl<UserForAdmin[]>(this.userToEdit?.contacts ?? []),
    });

    constructor(private service: AdminService) {}

    ngOnInit(): void {
        this.users = this.service.getAllUsers(0, 10);
        this.service.getNumberOfUsers().subscribe(numberOfUsers => {
            this.dataLength = numberOfUsers;
        });
    }

    setContactsOfUserToEdit(user: UserForAdmin): void{
        this.userToEdit = user;
        this.service.getContactsOfUser(user).subscribe(contacts => {
            this.userToEdit!!.contacts = contacts;
        });
    }

    handlePageEvent(event: PageEvent): void{
        this.defaultPageSize = event.pageSize;
        this.users = this.service.getAllUsers(event.pageIndex, event.pageSize);
    }
    hasRequestedRole(user: UserForAdmin){
        return user.roles.some(role => role.includes('REQUEST'));
    }

}
