import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableComponent } from './table/table.component';
import { NgxPaginationModule, PaginatePipe } from 'ngx-pagination';
import { Ng2SearchPipeModule} from 'ng2-search-filter'; 
import { FormsModule } from '@angular/forms';
import { SearchPipePipe } from './search-pipe.pipe';
import { PDFExportModule } from '@progress/kendo-angular-pdf-export';
import { SortPipePipe } from './sort-pipe.pipe';
import { SignalTableComponent } from './signal-table/table.component';
import { ExcludeElementPipe } from './excludeElement.pipe';
import { MatIconModule, MatTooltipModule, MatSelectModule, MatButtonModule } from '@angular/material';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgxPaginationModule,
    Ng2SearchPipeModule,
    PDFExportModule,
    MatIconModule,
    MatTooltipModule,
    MatSelectModule,
    MatButtonModule
  
  ],
  declarations: [TableComponent, SearchPipePipe,SortPipePipe, SignalTableComponent, ExcludeElementPipe],
  schemas: [],
  providers: [],
  exports: [TableComponent, SearchPipePipe, SortPipePipe, SignalTableComponent, ExcludeElementPipe]
})
export class TableModule {
  // public static forRoot(config): ModuleWithProviders {
  //   return {
  //     ngModule: TableModule,
  //     providers: [
  //       TableService,
  //       { provide: 'config', useValue: config }
  //     ]
  //   };
  // }
}
