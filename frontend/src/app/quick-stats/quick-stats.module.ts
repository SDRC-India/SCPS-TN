import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { QuickStatsRoutingModule } from './quick-stats-routing.module';
import { QuickStatsComponent } from './quick-stats/quick-stats.component';
import { TableModule } from 'lib/public_api';
import {MatCardModule } from '@angular/material';

@NgModule({
  declarations: [QuickStatsComponent],
  imports: [
    CommonModule,
    QuickStatsRoutingModule,
    TableModule,
    MatCardModule,
  ]
})
export class QuickStatsModule { }
