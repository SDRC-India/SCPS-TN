import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ReportComponent } from './report/report.component';
import { RawReportComponent } from './raw-report/raw-report.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';

const routes: Routes = [
  {
    path: '',
    component: ReportComponent,
    pathMatch: 'full'
  },
  {
    path: 'raw-report',
    component: RawReportComponent,
    pathMatch: 'full',
    canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["ALL_FORM_RAWDATA_DOWNLOAD"]
        }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
