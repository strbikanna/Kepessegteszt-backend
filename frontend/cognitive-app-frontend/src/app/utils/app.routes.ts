import {Routes} from '@angular/router';
import {HomeComponent} from "../general/home/home.component";
import {ProfilePageComponent} from "../pages/common/profile/profile-page.component";
import {CognitiveProfilePageComponent} from "../pages/common/cognitive-profile/cognitive-profile-page.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../general/not-found/not-found.component";
import {GameManagementPageComponent} from "../pages/admin/game-management/game-management-page.component";
import {EditGameFormComponent} from "../pages/admin/game-management/edit-game-form/edit-game-form.component";
import {editorGuard} from "../auth/editor.guard";
import {RecommendationPageComponent} from "../pages/admin/recommendation/recommendation-page.component";
import {ProfileDataComparisonPageComponent} from "../pages/common/profile-data-comparison/profile-data-comparison-page.component";
import {ResultPageComponent} from "../pages/common/result/result-page/result-page.component";
import {AdminPageComponent} from "../pages/admin/admin-page/admin-page.component";
import {adminAuthGuard} from "../auth/admin-auth.guard";


export const appRoutes: Routes = [
    {path: 'admin', component: AdminPageComponent, canActivate: [adminAuthGuard]},
    {path: 'game-management', component: GameManagementPageComponent, canActivate: [editorGuard], title: 'Játékok kezelése'},
    {path: 'edit-game/:id', component: EditGameFormComponent, canActivate: [editorGuard], title: 'Játék szerkesztése'},
    {path: 'edit-game', component: EditGameFormComponent, canActivate: [editorGuard], title: 'Játék szerkesztése'},
    {path: '', component: HomeComponent, title: 'Cognitive App'},
    {path: 'profile', component: ProfilePageComponent, canActivate: [loggedInGuard], title: 'Profil'},
    {path: 'profile-compare', component: ProfileDataComparisonPageComponent, canActivate: [loggedInGuard], title: 'Profil összehasonlítás'},
    {path: 'cognitive-profile', component: CognitiveProfilePageComponent, canActivate: [loggedInGuard], title: 'Kognitív profil'},
    {path: 'recommendation', component: RecommendationPageComponent, canActivate: [loggedInGuard], title: 'Játékok ajánlása'},
    {path: 'result', component: ResultPageComponent, canActivate: [loggedInGuard], title: 'Eredmények megtekintése'},
    {path: '**', component: NotFoundComponent}
];
