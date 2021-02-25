import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuickStatsServiceService } from '../service/quick-stats-service.service';
import { Constants } from 'src/app/constants';

@Component({
  selector: 'app-quick-stats',
  templateUrl: './quick-stats.component.html',
  styleUrls: ['./quick-stats.component.scss']
})
export class QuickStatsComponent implements OnInit {

  tableData: any;
  tableColumns: string[];
  quickStarts: any;
  objQuickStarts: any = {};
  area: string=Constants.STATE_NAME;
  districts: any[] = []
  selectedDistrict: any;
  totalData: any;
  excelFileName=Constants.QUICK_STATS_FILE_NAME_PREFIX+Constants.STATE_NAME;
  quickStatNote=Constants.QUICK_STATS_NOTE;

  constructor(private quickStatsServiceService:QuickStatsServiceService) { }

  ngOnInit() {
    this.quickStatsServiceService.getQuickStatData().subscribe((data) => {
      this.totalData = data.tableData;
      this.tableData = data.tableData;
      this.tableColumns = data.tableColumn;
      this.quickStarts = data.quickStats;
      this.districts = data.districts;
    });
    // this.area = JSON.parse(sessionStorage.getItem('userDetails')).sessionMap.area.areaName
  }


  districtSelected(district) {
    if (district) {
      this.excelFileName = Constants.QUICK_STATS_FILE_NAME_PREFIX +district.areaName
      this.tableData = this.totalData.filter(d => d.districtId == district.areaId);
    }
    else {
      this.excelFileName = Constants.QUICK_STATS_FILE_NAME_PREFIX +Constants.STATE_NAME
      this.tableData = this.totalData;
    }
  }

}
