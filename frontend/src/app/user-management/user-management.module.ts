import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserManagementRoutingModule } from './user-management-routing.module';
import {  MatInputModule, MatIconModule, MatFormFieldModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule, MatSlideToggle, MatSlideToggleModule, MatAutocompleteModule } from '@angular/material';

import { ReactiveFormsModule,FormsModule } from '@angular/forms'; 
import { UserManagementComponent } from './user-management/user-management.component';
import { UserSideMenuComponent } from './user-side-menu/user-side-menu.component';
import { AreaFilterPipe } from './filters/area-filter.pipe';
import { UserManagementService } from './services/user-management.service';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { FrequencyFilterPipe } from './filters/frequency-filter.pipe';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { AddFacilityComponent } from './add-facility/add-facility.component';
import { EditFacilityDetailsComponent } from './edit-facility-details/edit-facility-details.component';

@NgModule({
  imports: [
    CommonModule
    //,HttpModule
    //,HttpClientModule
    ,FormsModule
    ,UserManagementRoutingModule
    ,ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,        
    MatNativeDateModule,
    MatSlideToggleModule,
    MatAutocompleteModule      
    
  ],
  declarations: [
    UserManagementComponent,
    ResetPasswordComponent,
    UserSideMenuComponent,
    AreaFilterPipe,
    EditUserDetailsComponent,
    FrequencyFilterPipe,
    ChangePasswordComponent,
    AddFacilityComponent,
    EditFacilityDetailsComponent
  ],
  providers:[UserManagementService]
})
export class UserManagementModule { }
