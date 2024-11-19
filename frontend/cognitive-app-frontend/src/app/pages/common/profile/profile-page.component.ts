import {Component, inject, OnInit} from '@angular/core';
import {UserInfo} from "../../../auth/userInfo";
import {User} from "../../../model/user.model";
import {TEXTS} from "../../../utils/app.text_messages";
import {AuthUser} from "../../../model/user-contacts.model";
import {UserDataService} from "../../../service/user-data/user-data.service";
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators} from "@angular/forms";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AlertDialogComponent} from "../../../common/alert-dialog/alert-dialog.component";

@Component({
    selector: 'app-profile',
    templateUrl: './profile-page.component.html',
    styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent implements OnInit {
    user?: User
    email: string = ''

    text = TEXTS.user_info

    isDataChanged = false

    userForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        birthDate: [new Date()],
        city: ['', ],
        street: ['', ],
        houseNumber: ['', [Validators.pattern('^[0-9]{1,4}[a-zA-Z]{0,1}$')]],
        zip: ['', [ Validators.pattern('^[0-9]{4}$')]],
        gender: ['']
    }, {validators: this.createAddressValidator()});

    readonly dialog = inject(MatDialog);

    constructor(private service: UserDataService, private fb: FormBuilder) {
    }

    ngOnInit(): void {
        UserInfo.loginStatus.subscribe(isLoggedIn => {
            this.email = UserInfo.currentUser.email
        });
        this.service.getUserData().subscribe(user => {
            this.user = user
            this.initForm()
        })
    }

    initForm() {
        this.userForm.patchValue({
            firstName: this.user?.firstName,
            lastName: this.user?.lastName,
            email: this.email,
            birthDate: this.user?.birthDate,
            gender: this.user?.gender,
            city: this.user?.address?.city,
            street: this.user?.address?.street,
            houseNumber: this.user?.address?.houseNumber,
            zip: this.user?.address?.zip
        });
    }

    personalData = [
        {key: "firstName", text: this.text.first_name, value: this.user?.firstName, editable: true},
        {key: "lastName", text: this.text.last_name, value: this.user?.lastName, editable: true},
        {key: "email", text: this.text.email, value: this.email, editable: true},
        {key: undefined, text: this.text.username, value: UserInfo.currentUser.username, editable: false},
    ]

    rolesData = {text: this.text.roles, value: UserInfo.currentUser.roles.join(', '), editable: false}

    birthDateData = {key: "birthDate", text: this.text.birth_date, value: this.user?.birthDate, editable: true}

    genderData = {key: "gender", text: this.text.gender, value: this.user?.gender, editable: true}

    addressData = [
        {key: "city", text: this.text.city, value: this.user?.address?.city, editable: true},
        {key: "street", text: this.text.street, value: this.user?.address?.street, editable: true},
        {key: "houseNumber", text: this.text.house_number, value: this.user?.address?.houseNumber, editable: true},
        {key: "zip", text: this.text.zip, value: this.user?.address?.zip, editable: true},
    ]


    submitForm() {
        if (!this.user || !this.userForm.valid) return;
        const updatedUser: User = {
            firstName: this.userForm.get('firstName')?.value ?? this.user.firstName,
            lastName: this.userForm.get('lastName')?.value ?? this.user.lastName,
            username: this.user.username,
            birthDate: this.userForm.get('birthDate')?.value ?? this.user.birthDate,
            gender: this.userForm.get('gender')?.value ?? this.user.gender,
            address: undefined,
            roles: this.user.roles
        }
        if (this.userForm.get('city')?.value) {
            updatedUser.address = {
                city: this.userForm.get('city')!.value!!,
                street: this.userForm.get('street')!.value!!,
                houseNumber: this.userForm.get('houseNumber')!.value!!,
                zip: this.userForm.get('zip')!.value!!
            }
        }
        const authUser: AuthUser = {
            firstName: updatedUser.firstName,
            lastName: updatedUser.lastName,
            email: this.userForm.get('email')?.value ?? this.email,
            username: updatedUser.username,
            roles: [],
            contacts: [],
            id: -1
        }
        this.updateUser(updatedUser)
        if(this.email !== authUser.email || this.user.firstName !== authUser.firstName || this.user.lastName !== authUser.lastName) {
            this.updateAuthUser(authUser)
        }
        this.isDataChanged = false
    }

    updateAuthUser(user: AuthUser) {
        this.service.updateUserDataAuthServer(user).subscribe(user => {
            this.email = user.email
        });
    }

    updateUser(user: User) {
        this.service.updateUserData(user).subscribe(user => {
            this.user = user
            this.openSuccessDialog()
        });
    }

    openSuccessDialog() {
        this.dialog.open(AlertDialogComponent, {
            data: {
                title: this.text.success_title,
                message: this.text.success_message,
            },
        });
    }

    createAddressValidator() {
        return (control: AbstractControl): ValidationErrors | null => {
            const addressGroup = control as FormGroup
            const city = addressGroup.get('city')?.value
            const street = addressGroup.get('street')?.value
            const houseNumber = addressGroup.get('houseNumber')?.value
            const zip = addressGroup.get('zip')?.value
            if (city && street && houseNumber && zip) return null
            if (!(city || street || houseNumber || zip)) return null
            return {addressInvalid: true}
        }
    }

}
