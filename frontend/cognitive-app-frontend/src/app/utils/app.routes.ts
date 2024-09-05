import {Routes} from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";
import {RecommendedGamesComponent} from "../game/recommended-games-page/recommended-games.component";
import {CognitiveProfileComponent} from "../cognitive-profile/cognitive-profile.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../not-found/not-found.component";
import {GameManagementComponent} from "../game-management/game-management.component";
import {EditGameFormComponent} from "../game-management/edit-game-form/edit-game-form.component";
import {editorGuard} from "../auth/editor.guard";
import {RecommendationComponent} from "../recommendation/recommendation.component";
import {ProfileDataComparisonComponent} from "../profile-data-comparison/profile-data-comparison.component";
import {ResultPageComponent} from "../result/result-page/result-page.component";


export const appRoutes: Routes = [
    {path: 'game-management', component: GameManagementComponent, canActivate: [editorGuard], title: 'Játékok kezelése'},
    {path: 'edit-game/:id', component: EditGameFormComponent, canActivate: [editorGuard], title: 'Játék szerkesztése'},
    {path: 'edit-game', component: EditGameFormComponent, canActivate: [editorGuard], title: 'Játék szerkesztése'},
    {path: '', component: HomeComponent, title: 'Cognitive App'},
    {path: 'profile', component: ProfileComponent, canActivate: [loggedInGuard], title: 'Profil'},
    {path: 'profile-compare', component: ProfileDataComparisonComponent, canActivate: [loggedInGuard], title: 'Profil összehasonlítás'},
    {path: 'games', component: RecommendedGamesComponent, canActivate: [loggedInGuard], title: 'Játékok'},
    {path: 'cognitive-profile', component: CognitiveProfileComponent, canActivate: [loggedInGuard], title: 'Kognitív profil'},
    {path: 'recommendation', component: RecommendationComponent, canActivate: [loggedInGuard], title: 'Játékok ajánlása'},
    {path: 'result', component: ResultPageComponent, canActivate: [loggedInGuard], title: 'Eredmények megtekintése'},
    {path: '**', component: NotFoundComponent}
];
