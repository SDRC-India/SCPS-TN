import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Exception404Component } from './exception404/exception404.component';
import { LoginComponent } from './login/login.component';
import { LoggedinGuard } from './guard/loggedin.guard';
import { ForgotpassComponent } from './forgotpass/forgotpass.component';

const routes: Routes = [

  {
    path: '',
    component: LoginComponent,
    canActivate: [LoggedinGuard],
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [LoggedinGuard],
    pathMatch: 'full'
  },
  {
    path: 'error',
    component: Exception404Component,
    pathMatch: 'full'
  },
  {
    path: 'exception',
    component: Exception404Component,
    pathMatch: 'full'
  },
  {
    path: 'user',
    loadChildren: './user-management/user-management.module#UserManagementModule',
  },
  {
    path: 'report',
    loadChildren: './report/report.module#ReportModule',
  },
  {
    path: 'forgotpass',
    component: ForgotpassComponent,
    pathMatch: 'full'
  },
  {
    path: 'index',
    loadChildren: './index-view/index-view.module#IndexViewModule',
  }
  ,
  {
    path: 'quickStats',
    loadChildren: './quick-stats/quick-stats.module#QuickStatsModule',
  }
  ,
  {
    path: 'dashboard',
    loadChildren: './dashboard/dashboard.module#DashboardModule',
  }
  ,

  {
    path: 'login/forgotpass',
    component: ForgotpassComponent,
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
