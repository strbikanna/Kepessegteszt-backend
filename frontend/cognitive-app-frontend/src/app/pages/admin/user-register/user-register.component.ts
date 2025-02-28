import {Component, OnInit} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {AdminService} from "../../../service/admin/admin.service";
import {AuthUser} from "../../../model/user/user-contacts.model";
import {UserRegistrationData} from "../../../model/user/user_registration_data.model";
import {TEXTS} from "../../../text/app.text_messages";

@Component({
    selector: 'app-user-register',
    templateUrl: './user-register.component.html',
    styleUrls: ['./user-register.component.scss']
})
export class UserRegisterComponent implements OnInit {
    text = TEXTS.register_user_page;
    constructor(private fb: FormBuilder, private service: AdminService) {
    }

    contactList: AuthUser[] = [];
    registrationError = false;
    registrationSuccess = false;

    userRegistrationForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        username: ['', Validators.required],
        password: ['', Validators.required],
    });

    ngOnInit() {
        this.service.getContacts().subscribe(contacts => {
            this.contactList = contacts;
        });
    }

    registerUser() {
        const regData: UserRegistrationData = {
            firstName: this.userRegistrationForm.get('firstName')?.value!,
            lastName: this.userRegistrationForm.get('lastName')?.value!,
            username: this.userRegistrationForm.get('username')?.value!,
            password: this.userRegistrationForm.get('password')?.value!
        }
        this.service.registerUser(regData).subscribe({
            next: (registeredUser) => {
                this.contactList.push(registeredUser);
                this.registrationSuccess = true;
                setTimeout(() => {
                    this.registrationSuccess = false;
                }, 4000);
            },
            error: (error) => {
                this.registrationError = true;
                setTimeout(() => {
                    this.registrationError = false;
                }, 4000);
            }
        });
    }

}
