import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RoleGuardGuard } from '../guard/role-guard.guard';
import { NewBlankFormComponent } from './new-blank-form/new-blank-form.component';
import { DraftsComponent } from './drafts/drafts.component';
import { DataEntryRouteGuardGuard } from '../guard/data-entry-route-guard.guard';


const routes: Routes = [

      
      { 
        path: 'dataentry', 
        pathMatch: 'full', 
        component: NewBlankFormComponent,
        canActivate: [RoleGuardGuard,DataEntryRouteGuardGuard],
        data: { 
          expectedRoles: ["DATA_ENTRY"]
        }
      },
      { 
        path: 'drafts', 
        pathMatch: 'full', 
        component: DraftsComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["DATA_ENTRY"]
        }
      }
     
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DataCollectionRoutingModule { }
