import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { AuthModule, LogLevel } from 'angular-auth-oidc-client';
import {RouterModule} from "@angular/router";
import {appRoutes} from "./utils/app.routes";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from "@angular/material/icon";
import { UserInfoComponent } from './user-info/user-info.component';
import { ProfileComponent } from './profile/profile.component';
import {MatMenuModule} from "@angular/material/menu";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import { ImpersonationComponent } from './impersonation/impersonation.component';
import {AuthInterceptor} from "./auth/auth.interceptor";
import { GamesComponent } from './games/games.component';
import {MatTabsModule} from "@angular/material/tabs";
import { PlaygroundComponent } from './playground/playground.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatChipsModule} from "@angular/material/chips";
import {ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {NgOptimizedImage} from "@angular/common";
import {AdminModule} from "./admin/admin.module";
import { HeaderComponent } from './header/header.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    UserInfoComponent,
    ProfileComponent,
    ImpersonationComponent,
    GamesComponent,
    PlaygroundComponent,
    HeaderComponent,
  ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(appRoutes),
        HttpClientModule,
        AuthModule.forRoot({
            config: {
                configId: 'baseConfig',
                authority: 'http://localhost:9000',
                redirectUrl: 'http://localhost:4200',
                postLogoutRedirectUri: 'http://localhost:4200',
                clientId: 'frontend-client-002233',
                scope: 'openid',
                responseType: 'code',
                silentRenew: false,
                useRefreshToken: false,
                logLevel: LogLevel.Debug,
            },
        }),
        AdminModule,
        MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, BrowserAnimationsModule, MatCardModule, MatListModule, MatTabsModule, MatDialogModule, MatPaginatorModule, MatChipsModule, ReactiveFormsModule, MatInputModule, MatExpansionModule, MatCheckboxModule, MatAutocompleteModule, NgOptimizedImage,
    ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
