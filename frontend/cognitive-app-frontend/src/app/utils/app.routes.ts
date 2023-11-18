import {Routes} from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";
import {RecommendedGamesComponent} from "../game/recommended-games-page/recommended-games.component";
import {PlaygroundComponent} from "../game/playground/playground.component";
import {gameGuard} from "../auth/game.guard";
import {CognitiveProfileComponent} from "../cognitive-profile/cognitive-profile.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../not-found/not-found.component";
import {GameManagementComponent} from "../game-management/game-management.component";
import {EditGameFormComponent} from "../game-management/edit-game-form/edit-game-form.component";
import {editorGuard} from "../auth/editor.guard";


export const appRoutes: Routes = [
    {path: 'game-management', component: GameManagementComponent, canActivate: [editorGuard], title: 'Játékok kezelése'},
    {path: 'edit-game/:id', component: EditGameFormComponent, canActivate: [editorGuard], title: 'Játék szerkesztése'},
    {path: '', component: HomeComponent, title: 'Cognitive App'},
    {path: 'profile', component: ProfileComponent, canActivate: [loggedInGuard], title: 'Profil'},
    {path: 'games', component: RecommendedGamesComponent, canActivate: [loggedInGuard], title: 'Játékok'},
    {path: 'cognitive-profile', component: CognitiveProfileComponent, canActivate: [loggedInGuard], title: 'Kognitív profil'},
    {path: 'playground', component: PlaygroundComponent, canActivate: [gameGuard], title: 'Játékterület'},
    {path: '**', component: NotFoundComponent}
];
