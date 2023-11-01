

import { Routes } from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";
import {GamesComponent} from "../game/games-page/games.component";
import {PlaygroundComponent} from "../game/playground/playground.component";
import {gameGuard} from "../auth/game.guard";


export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'games', component: GamesComponent },
  { path: 'playground', component: PlaygroundComponent, canActivate: [gameGuard] },
];
