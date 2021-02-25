import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PerformanceViewComponent } from './performance-view/performance-view.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';
import { AuthGuard } from '../guard/auth.guard';
import { ThematicDashboardComponent } from './thematic-dashboard/thematic-dashboard.component';

const routes: Routes = [
  {
    path: 'performance-view', 
    pathMatch: 'full', 
    component: PerformanceViewComponent,
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["DASHBOARD_VIEW"]
        }
  },
  {
    path: 'thematic-view', 
    pathMatch: 'full', 
    component: ThematicDashboardComponent,
    // canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["DASHBOARD_VIEW"]
        }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
