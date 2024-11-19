import {ErrorHandler, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { LoginComponent } from './general/login/login.component';
import { HomeComponent } from './general/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { AuthModule, LogLevel } from 'angular-auth-oidc-client';
import {provideRouter, RouterModule, withComponentInputBinding} from "@angular/router";
import {appRoutes} from "./utils/app.routes";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from "@angular/material/icon";
import { ProfilePageComponent } from './pages/common/profile/profile-page.component';
import {MatMenuModule} from "@angular/material/menu";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import { ImpersonationComponent } from './general/impersonation/impersonation.component';
import {AuthInterceptor} from "./auth/auth.interceptor";
import {MatTabsModule} from "@angular/material/tabs";
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from "@angular/material/dialog";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatChipsModule} from "@angular/material/chips";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import { HeaderComponent } from './general/header/header.component';
import {MatProgressBarModule} from "@angular/material/progress-bar";
import { AlertDialogComponent } from './common/alert-dialog/alert-dialog.component';
import {GlobalErrorhandlerService} from "./utils/global-errorhandler.service";
import { CognitiveProfilePageComponent } from './pages/student/cognitive-profile/cognitive-profile-page.component';
import * as echarts from 'echarts';
import { NgxEchartsModule } from 'ngx-echarts';
import { ProfileChartComponent } from './charts/profile-chart/profile-chart.component';
import { ProfileCardComponent } from './common/profile-card/profile-card.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import { NotFoundComponent } from './general/not-found/not-found.component';
import { GameManagementPageComponent } from './pages/admin/game-management/game-management-page.component';
import { FileUploadComponent } from './common/file-upload/file-upload.component';
import { EditGameFormComponent } from './pages/admin/game-management/edit-game-form/edit-game-form.component';
import { GameCardComponent } from './common/game-card/game-card.component';
import { CalculationDialogComponent } from './pages/admin/game-management/calculation-dialog/calculation-dialog.component';
import { RecommendationPageComponent } from './pages/admin/recommendation/recommendation-page.component';
import {MatDividerModule} from "@angular/material/divider";
import {environment} from "../environments/environment";
import { ProfileDataComparisonPageComponent } from './pages/student/profile-data-comparison/profile-data-comparison-page.component';
import { ProfileRadarChartComponent } from './charts/profile-radar-chart/profile-radar-chart.component';
import {MatRadioModule} from "@angular/material/radio";
import { ConfigItemFormComponent } from './pages/admin/game-management/config-item-form/config-item-form.component';
import { ResultInfoCardComponent } from './pages/common/result/result-info-card/result-info-card.component';
import { ResultPageComponent } from './pages/common/result/result-page/result-page.component';
import { ConfirmDialogComponent } from './common/confirm-dialog/confirm-dialog.component';
import { CsvDownloadButtonComponent } from './common/csv-download-button/csv-download-button.component';
import {MatTooltipModule} from '@angular/material/tooltip';
import { SortControlComponent } from './common/sort-control/sort-control.component';
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import { FilterControlComponent } from './common/filter-control/filter-control.component';
import {CdkFixedSizeVirtualScroll, CdkVirtualScrollViewport} from "@angular/cdk/scrolling";
import {MatSidenavModule} from "@angular/material/sidenav";
import { GameSearchComponent } from './common/game-search/game-search.component';
import { RecommendationDetailCardComponent } from './common/recommendation-detail-card/recommendation-detail-card.component';
import { UserFilterComponent } from './common/user-filter/user-filter.component';
import { ProfileLineChartComponent } from './charts/profile-line-chart/profile-line-chart.component';
import { RatioScaleComponent } from './charts/ratio-scale/ratio-scale.component';
import {MatSliderModule} from "@angular/material/slider";
import { UserAutocompleteComponent } from './common/user-autocomplete/user-autocomplete.component';
import { CandlestickChartComponent } from './charts/candlestick-chart/candlestick-chart.component';
import {AdminPageComponent} from "./pages/admin/admin-page/admin-page.component";
import {UserSearchComponent} from "./common/user-search/user-search.component";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { AdminCognitiveProfilePageComponent } from './pages/admin/admin-cognitive-profile/admin-cognitive-profile-page.component';
import { DatePickerComponent } from './common/date-picker/date-picker.component';
import { UpAndDownButtonComponent } from './common/up-and-down-button/up-and-down-button.component';
import { AdminProfileDataComparisonPageComponent } from './pages/admin/admin-profile-data-comparison/admin-profile-data-comparison-page.component';
import {MatTableModule} from "@angular/material/table";
import { SideMenuComponent } from './general/side-menu/side-menu.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        ProfilePageComponent,
        HeaderComponent,
        ImpersonationComponent,
        AlertDialogComponent,
        CognitiveProfilePageComponent,
        ProfileChartComponent,
        ProfileCardComponent,
        NotFoundComponent,
        GameManagementPageComponent,
        FileUploadComponent,
        EditGameFormComponent,
        GameCardComponent,
        CalculationDialogComponent,
        RecommendationPageComponent,
        ProfileDataComparisonPageComponent,
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
        UserFilterComponent,
        ProfileLineChartComponent,
        RatioScaleComponent,
        UserAutocompleteComponent,
        CandlestickChartComponent,
        AdminPageComponent,
        UserSearchComponent,
        AdminCognitiveProfilePageComponent,
        DatePickerComponent,
        UpAndDownButtonComponent,
        AdminProfileDataComparisonPageComponent,
        SideMenuComponent
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
        MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, BrowserAnimationsModule, MatCardModule, MatListModule, MatTabsModule, MatDialogModule, MatPaginatorModule, MatChipsModule, ReactiveFormsModule, MatInputModule, MatExpansionModule, MatCheckboxModule, MatAutocompleteModule, MatProgressBarModule,
        NgxEchartsModule.forRoot({
            echarts
        }),
        MatDatepickerModule, MatNativeDateModule, MatSelectModule, MatDividerModule, MatRadioModule, MatTooltipModule, MatButtonToggleModule, FormsModule, CdkVirtualScrollViewport, CdkFixedSizeVirtualScroll, MatSidenavModule, MatSliderModule, MatSnackBarModule, MatTableModule
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
