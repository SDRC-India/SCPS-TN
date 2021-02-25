import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { QuickStatsComponent } from './quick-stats/quick-stats.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';

const routes: Routes = [
  {
    path: '',
    component: QuickStatsComponent,
    pathMatch: 'full',
    canActivate: [RoleGuardGuard],
    data: { 
      expectedRoles: ['QUICK_STATS']
    }
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class QuickStatsRoutingModule { }
