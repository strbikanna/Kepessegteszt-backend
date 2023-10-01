

import { Routes } from '@angular/router';
import {HomeComponent} from "../home/home.component";
import {ProfileComponent} from "../profile/profile.component";


export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
];
