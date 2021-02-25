import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { PerformanceViewComponent } from './performance-view/performance-view.component';
import { ThematicMapComponent } from './thematic-map/thematic-map.component';
import { DashboardService } from './dashboard.service';
import { LineChartComponent } from './line-chart/line-chart.component';
import { MatFormFieldModule, MatSelectModule, MAT_SELECT_SCROLL_STRATEGY, MatTabsModule, MatIconModule, MatTooltipModule, MatTableModule, MatSortModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BarChartComponent } from './bar-chart/bar-chart.component';
import { HorizontalBarChartComponent } from './horizontal-bar-chart/horizontal-bar-chart.component';
import { Overlay, BlockScrollStrategy } from '@angular/cdk/overlay';
import { ThematicDashboardComponent } from './thematic-dashboard/thematic-dashboard.component';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { SearchValuePipe } from './pipes/search-value.pipe';
import { DashboardLineChartComponent } from './dashboard-line-chart/dashboard-line-chart.component';
import { TableModule } from 'lib/public_api';

@NgModule({
  declarations: [ PerformanceViewComponent, ThematicMapComponent, LineChartComponent, BarChartComponent, HorizontalBarChartComponent, ThematicDashboardComponent, SearchValuePipe, DashboardLineChartComponent],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTabsModule,
    NgxMatSelectSearchModule,
    MatIconModule,
    MatTooltipModule,MatTableModule,MatSortModule,TableModule
  ],
  providers: [DashboardService, { provide: MAT_SELECT_SCROLL_STRATEGY, useFactory: scrollFactory, deps: [Overlay] }]
})
export class DashboardModule { }
export function scrollFactory(overlay: Overlay): () => BlockScrollStrategy {
  return () => overlay.scrollStrategies.block();
}