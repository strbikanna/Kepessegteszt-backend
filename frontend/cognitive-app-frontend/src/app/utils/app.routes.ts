import {Routes} from '@angular/router';
import {HomeComponent} from "../general/home/home.component";
import {ProfilePageComponent} from "../pages/common/profile/profile-page.component";
import {CognitiveProfilePageComponent} from "../pages/student/cognitive-profile/cognitive-profile-page.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../general/not-found/not-found.component";
import {GameManagementPageComponent} from "../pages/admin/game-management/game-management-page.component";
import {EditGameFormComponent} from "../pages/admin/game-management/edit-game-form/edit-game-form.component";
import {adminCognitiveProfileGuard, gameManagementGuard, groupManagementGuard} from "../auth/gameManagementGuard";
import {RecommendationPageComponent} from "../pages/admin/recommendation/recommendation-page.component";
import {ProfileDataComparisonPageComponent} from "../pages/student/profile-data-comparison/profile-data-comparison-page.component";
import {ResultPageComponent} from "../pages/common/result/result-page/result-page.component";
import {AdminPageComponent} from "../pages/admin/admin-page/admin-page.component";
import {adminAuthGuard} from "../auth/admin-auth.guard";
import {
    AdminCognitiveProfilePageComponent
} from "../pages/admin/admin-cognitive-profile/admin-cognitive-profile-page.component";
import {
    AdminProfileDataComparisonPageComponent
} from "../pages/admin/admin-profile-data-comparison/admin-profile-data-comparison-page.component";
import {GroupManagementComponent} from "../pages/admin/group-management/group-management.component";
import {PrivacyPolicyPageComponent} from "../general/privacy-policy-page/privacy-policy-page.component";
import {DeleteAccountComponent} from "../pages/common/delete-account/delete-account.component";


export const appRoutes: Routes = [
    {path: 'admin', component: AdminPageComponent, canActivate: [adminAuthGuard], title: 'Felhasználók kezelése'},
    {path: 'game-management', component: GameManagementPageComponent, canActivate: [gameManagementGuard], title: 'Játékok kezelése'},
    {path: 'edit-game/:id', component: EditGameFormComponent, canActivate: [gameManagementGuard], title: 'Játék szerkesztése'},
    {path: 'edit-game', component: EditGameFormComponent, canActivate: [gameManagementGuard], title: 'Játék szerkesztése'},
    {path: '', component: HomeComponent, title: 'Cognitive App'},
    {path: 'profile', component: ProfilePageComponent, canActivate: [loggedInGuard], title: 'Profil'},
    {path: 'profile-compare', component: ProfileDataComparisonPageComponent, canActivate: [loggedInGuard], title: 'Profil összehasonlítás'},
    {path: 'profile-compare-admin', component: AdminProfileDataComparisonPageComponent, canActivate: [loggedInGuard, adminCognitiveProfileGuard], title: 'Profil összehasonlítás'},
    {path: 'cognitive-profile', component: CognitiveProfilePageComponent, canActivate: [loggedInGuard], title: 'Kognitív profil'},
    {path: 'cognitive-profile-admin', component: AdminCognitiveProfilePageComponent, canActivate: [loggedInGuard, adminCognitiveProfileGuard], title: 'Kognitív profil'},
    {path: 'recommendation', component: RecommendationPageComponent, canActivate: [loggedInGuard], title: 'Játékok ajánlása'},
    {path: 'result', component: ResultPageComponent, canActivate: [loggedInGuard], title: 'Eredmények megtekintése'},
    {path: 'group-management', component: GroupManagementComponent, canActivate: [loggedInGuard, groupManagementGuard], title: 'Felhasználói csoportok kezelése'},
    {path: 'privacy-policy', component: PrivacyPolicyPageComponent, title: 'Adatvédelmi irányelvek'},
    {path: 'delete-account', component: DeleteAccountComponent, title: 'Felhasználói fiók eltávolítása', canActivate: [loggedInGuard]},
    {path: '**', component: NotFoundComponent}
];
