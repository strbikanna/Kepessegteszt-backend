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


export const appRoutes: Routes = [
    {path: 'game-management', component: GameManagementComponent,},
    {path: 'edit-game/:id', component: EditGameFormComponent},
    {path: '', component: HomeComponent},
    {path: 'profile', component: ProfileComponent, canActivate: [loggedInGuard]},
    {path: 'games', component: RecommendedGamesComponent, canActivate: [loggedInGuard]},
    {path: 'cognitive-profile', component: CognitiveProfileComponent, canActivate: [loggedInGuard]},
    {path: 'playground', component: PlaygroundComponent, canActivate: [gameGuard]},
    {path: '**', component: NotFoundComponent}
];
