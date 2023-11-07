

import { Routes } from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";
import {GamesComponent} from "../game/games-page/games.component";
import {PlaygroundComponent} from "../game/playground/playground.component";
import {gameGuard} from "../auth/game.guard";
import {CognitiveProfileComponent} from "../cognitive-profile/cognitive-profile.component";
import {loggedInGuard} from "../auth/logged-in.guard";
import {NotFoundComponent} from "../not-found/not-found.component";



export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [loggedInGuard] },
  { path: 'games', component: GamesComponent, canActivate: [loggedInGuard] },
  { path: 'cognitive-profile', component: CognitiveProfileComponent, canActivate: [loggedInGuard] },
  { path: 'playground', component: PlaygroundComponent, canActivate: [gameGuard] },
  { path: '**', component: NotFoundComponent}
];
