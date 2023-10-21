import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminPageComponent} from "./admin-page/admin-page.component";
import {adminAuthGuard} from "../auth/admin-auth.guard";

const routes: Routes = [
  {path: 'admin', component: AdminPageComponent, canActivate: [adminAuthGuard]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
