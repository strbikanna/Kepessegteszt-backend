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
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from "@angular/material/dialog";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatChipsModule} from "@angular/material/chips";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
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
import { RecommendationComponent } from './recommendation/recommendation.component';
import {MatDividerModule} from "@angular/material/divider";
import {environment} from "../environments/environment";
import { ProfileDataComparisonComponent } from './profile-data-comparison/profile-data-comparison.component';
import { ProfileRadarChartComponent } from './profile-data-comparison/profile-radar-chart/profile-radar-chart.component';
import {MatRadioModule} from "@angular/material/radio";
import { ConfigItemFormComponent } from './game-management/config-item-form/config-item-form.component';
import { ResultInfoCardComponent } from './result/result-info-card/result-info-card.component';
import { ResultPageComponent } from './result/result-page/result-page.component';
import { ConfirmDialogComponent } from './common/confirm-dialog/confirm-dialog.component';
import { CsvDownloadButtonComponent } from './common/csv-download-button/csv-download-button.component';
import {MatTooltipModule} from '@angular/material/tooltip';
import { SortControlComponent } from './common/sort-control/sort-control.component';
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { FilterControlComponent } from './common/filter-control/filter-control.component';
import {CdkFixedSizeVirtualScroll, CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {MatSidenavModule} from "@angular/material/sidenav";
import { GameSearchComponent } from './game-management/game-search/game-search.component';
import { RecommendationDetailCardComponent } from './recommendation-detail-card/recommendation-detail-card.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        ProfileComponent,
        RecommendedGamesComponent,
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
        RecommendationComponent,
        ProfileDataComparisonComponent,
        ProfileRadarChartComponent,
        ConfigItemFormComponent,
        ResultInfoCardComponent,
        ResultPageComponent,
        ConfirmDialogComponent,
        CsvDownloadButtonComponent,
        SortControlComponent,
        FilterControlComponent,
        GameSearchComponent,
        RecommendationDetailCardComponent,
    ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(appRoutes),
        HttpClientModule,
        AuthModule.forRoot({
            config: [{
                configId: 'baseConfig',
                authority: environment.authServerUrl,
                redirectUrl: environment.clientUrl,
                postLogoutRedirectUri: environment.clientUrl,
                clientId: environment.clientId,
                scope: 'openid',
                responseType: 'code',
                silentRenew: true,
                useRefreshToken: false,
                silentRenewUrl: `${environment.clientUrl}/silent-renew.html`,
                renewTimeBeforeTokenExpiresInSeconds: 10,
                logLevel: LogLevel.Debug,
            },
                {
                    configId: 'gameTokenConfig',
                    authority: environment.authServerUrl,
                    redirectUrl: `${environment.clientUrl}/games`,
                    postLogoutRedirectUri: environment.clientUrl,
                    clientId: environment.clientId,
                    scope: 'openid game',
                    responseType: 'code',
                    silentRenew: true,
                    silentRenewUrl: `${environment.clientUrl}/silent-renew.html`,
                    useRefreshToken: false,
                    logLevel: LogLevel.Debug,
                },
            ],
        }),
        AdminModule,
        MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, BrowserAnimationsModule, MatCardModule, MatListModule, MatTabsModule, MatDialogModule, MatPaginatorModule, MatChipsModule, ReactiveFormsModule, MatInputModule, MatExpansionModule, MatCheckboxModule, MatAutocompleteModule, MatProgressBarModule,
        NgxEchartsModule.forRoot({
            echarts
        }),
        MatDatepickerModule, MatNativeDateModule, MatSelectModule, MatDividerModule, MatRadioModule, MatTooltipModule, MatButtonToggleModule, FormsModule, CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, MatSidenavModule
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
