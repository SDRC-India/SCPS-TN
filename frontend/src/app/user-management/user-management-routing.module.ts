import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserManagementComponent } from './user-management/user-management.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { RoleGuardGuard } from '../guard/role-guard.guard';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { AddFacilityComponent } from './add-facility/add-facility.component';
import { EditFacilityDetailsComponent } from './edit-facility-details/edit-facility-details.component';

const routes: Routes = [

      
      { 
        path: 'user-management', 
        pathMatch: 'full', 
        component: UserManagementComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API", "CREATE_USER"]
        }
      },
      { 
        path: 'reset-password', 
        pathMatch: 'full', 
        component: ResetPasswordComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'edit-user', 
        pathMatch: 'full', 
        component: EditUserDetailsComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'change-password', 
        pathMatch: 'full', 
        component: ChangePasswordComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'add-facility', 
        pathMatch: 'full', 
        component: AddFacilityComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'edit-facility', 
        pathMatch: 'full', 
        component: EditFacilityDetailsComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserManagementRoutingModule { }
