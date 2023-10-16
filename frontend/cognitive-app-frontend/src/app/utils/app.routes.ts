

import { Routes } from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";
import {GamesComponent} from "../games/games.component";
import {PlaygroundComponent} from "../playground/playground.component";
import {AdminPageComponent} from "../admin-page/admin-page.component";


export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'games', component: GamesComponent },
  { path: 'playground', component: PlaygroundComponent },
  { path: 'admin', component: AdminPageComponent },

];
