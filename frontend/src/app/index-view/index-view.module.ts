import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { IndexViewRoutingModule } from './index-view-routing.module';
import { IndexViewComponent } from './index-view/index-view.component';
import { AreaFilterPipe } from './pipes/area-filter.pipe';
import { SectorFilterPipe } from './pipes/sector-filter.pipe';
import { FormsModule } from '@angular/forms';
import { MatSelectModule, MAT_SELECT_SCROLL_STRATEGY, MatIconModule } from '@angular/material';
import { TableModule } from 'lib/public_api';
import { Overlay, BlockScrollStrategy } from '@angular/cdk/overlay';

@NgModule({
  declarations: [IndexViewComponent, AreaFilterPipe, SectorFilterPipe],
  imports: [
    CommonModule,
    IndexViewRoutingModule,
    FormsModule,
    MatSelectModule,
    TableModule,
    MatIconModule
  ],
  providers: [{ provide: MAT_SELECT_SCROLL_STRATEGY, useFactory: scrollFactory, deps: [Overlay] }]
})
export class IndexViewModule { }
export function scrollFactory(overlay: Overlay): () => BlockScrollStrategy {
  return () => overlay.scrollStrategies.block();
}