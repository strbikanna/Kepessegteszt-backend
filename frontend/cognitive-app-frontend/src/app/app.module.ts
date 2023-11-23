import {ErrorHandler, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { AuthModule, LogLevel } from 'angular-auth-oidc-client';
import {provideRouter, RouterModule, withComponentInputBinding} from "@angular/router";
import {appRoutes} from "./utils/app.routes";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from "@angular/material/icon";
import { ProfileComponent } from './profile/profile.component';
import {MatMenuModule} from "@angular/material/menu";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import { ImpersonationComponent } from './impersonation/impersonation.component';
import {AuthInterceptor} from "./auth/auth.interceptor";
import { RecommendedGamesComponent } from './game/recommended-games-page/recommended-games.component';
import {MatTabsModule} from "@angular/material/tabs";
import { PlaygroundComponent } from './game/playground/playground.component';
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from "@angular/material/dialog";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatChipsModule} from "@angular/material/chips";
import {ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {AdminModule} from "./admin/admin.module";
import { HeaderComponent } from './header/header.component';
import {MatProgressBarModule} from "@angular/material/progress-bar";
import { AlertDialogComponent } from './common/alert-dialog/alert-dialog.component';
import {GlobalErrorhandlerService} from "./utils/global-errorhandler.service";
import { CognitiveProfileComponent } from './cognitive-profile/cognitive-profile.component';
import * as echarts from 'echarts';
import { NgxEchartsModule } from 'ngx-echarts';
import { ProfileChartComponent } from './cognitive-profile/profile-chart/profile-chart.component';
import { ProfileCardComponent } from './cognitive-profile/profile-card/profile-card.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import { NotFoundComponent } from './not-found/not-found.component';
import { GameManagementComponent } from './game-management/game-management.component';
import { FileUploadComponent } from './common/file-upload/file-upload.component';
import { EditGameFormComponent } from './game-management/edit-game-form/edit-game-form.component';
import { GameCardComponent } from './common/game-card/game-card.component';
import { CalculationDialogComponent } from './game-management/calculation-dialog/calculation-dialog.component';
import {MatDividerModule} from "@angular/material/divider";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    ProfileComponent,
    RecommendedGamesComponent,
    PlaygroundComponent,
    HeaderComponent,
    ImpersonationComponent,
    AlertDialogComponent,
    CognitiveProfileComponent,
    ProfileChartComponent,
    ProfileCardComponent,
    NotFoundComponent,
    GameManagementComponent,
    FileUploadComponent,
    EditGameFormComponent,
    GameCardComponent,
    CalculationDialogComponent,
  ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(appRoutes),
        HttpClientModule,
        AuthModule.forRoot({
            config: [{
                configId: 'baseConfig',
                authority: 'http://localhost:9000',
                redirectUrl: 'http://localhost:4200',
                postLogoutRedirectUri: 'http://localhost:4200',
                clientId: 'frontend-client-002233',
                scope: 'openid',
                responseType: 'code',
                silentRenew: true,
                useRefreshToken: true,
                logLevel: LogLevel.Debug,
            },
                {
                    configId: 'gameTokenConfig',
                    authority: 'http://localhost:9000',
                    redirectUrl: 'http://localhost:4200/games',
                    postLogoutRedirectUri: 'http://localhost:4200',
                    clientId: 'frontend-client-002233',
                    scope: 'openid game',
                    responseType: 'code',
                    silentRenew: true,
                    useRefreshToken: true,
                    logLevel: LogLevel.Debug,
                },
            ],
        }),
        AdminModule,
        MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, BrowserAnimationsModule, MatCardModule, MatListModule, MatTabsModule, MatDialogModule, MatPaginatorModule, MatChipsModule, ReactiveFormsModule, MatInputModule, MatExpansionModule, MatCheckboxModule, MatAutocompleteModule, MatProgressBarModule,
        NgxEchartsModule.forRoot({
            echarts
        }),
        MatDatepickerModule, MatNativeDateModule, MatSelectModule, MatDividerModule
    ],
  providers: [
      {
        provide: HTTP_INTERCEPTORS,
        useClass: AuthInterceptor,
        multi: true
      },
    {
      provide: MAT_DIALOG_DEFAULT_OPTIONS,
      useValue: {
        disableClose: false,
        hasBackdrop: true
      }
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorhandlerService,
    },
    provideRouter(appRoutes, withComponentInputBinding()),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
