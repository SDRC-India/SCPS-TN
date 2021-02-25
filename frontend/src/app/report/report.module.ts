import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyDatePickerModule } from 'mydatepicker';
import { ReportRoutingModule } from './report-routing.module';
import { ReportComponent } from './report/report.component';
import { RawReportComponent } from './raw-report/raw-report.component';
import { MatFormFieldModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule, MatCheckboxModule, MatInputModule, MatButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReportService } from './report.service';

@NgModule({
  declarations: [ReportComponent, RawReportComponent],
  imports: [
    CommonModule,
    FormsModule,
    MyDatePickerModule,
    ReactiveFormsModule,
    ReportRoutingModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule
  ],
  providers: [
    ReportService
  ]
})
export class ReportModule { }
