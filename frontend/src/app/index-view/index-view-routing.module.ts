import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IndexViewComponent } from './index-view/index-view.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';

const routes: Routes = [
  {
    path: '',
    component: IndexViewComponent,
    pathMatch: 'full',
    // canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ['DASHBOARD_VIEW']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class IndexViewRoutingModule { }
