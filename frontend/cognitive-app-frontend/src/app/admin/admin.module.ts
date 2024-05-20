import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {AdminRoutingModule} from './admin-routing.module';
import {AdminPageComponent} from "./admin-page/admin-page.component";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatFormFieldModule} from "@angular/material/form-field";
import {ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatChipsModule} from "@angular/material/chips";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatIconModule} from "@angular/material/icon";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatButtonModule} from "@angular/material/button";
import {MatDividerModule} from "@angular/material/divider";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {UserSearchComponent} from "./common/user_contact_autocomplete/user-search/user-search.component";


@NgModule({
    imports: [
        CommonModule,
        AdminRoutingModule,
        MatExpansionModule,
        MatCheckboxModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        MatChipsModule,
        MatPaginatorModule,
        MatIconModule,
        MatAutocompleteModule,
        MatButtonModule,
        MatDividerModule,
        MatSnackBarModule,
    ],
    declarations: [
        AdminPageComponent,
        UserSearchComponent
    ],
    exports: [
        UserSearchComponent
    ]
})
export class AdminModule {
}
