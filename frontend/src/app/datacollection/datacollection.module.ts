import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewBlankFormComponent } from './new-blank-form/new-blank-form.component';
import { DataCollectionRoutingModule } from './datacollection-routing.module';
import { FormsModule } from '@angular/forms';
import { DraftsComponent } from './drafts/drafts.component';
import { MatButtonModule, MatButtonToggleModule, MatInputModule, MatSelectModule, MatFormFieldModule, MatTextareaAutosize } from '@angular/material';
import { ConstraintTokenizer } from './engine/constraintTokenizer';
@NgModule({
  declarations: [NewBlankFormComponent, DraftsComponent],
  imports: [
    CommonModule,
    DataCollectionRoutingModule,
    FormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule

    
    
    
  ]
})
export class DatacollectionModule { }
