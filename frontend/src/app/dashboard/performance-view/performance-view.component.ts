import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../dashboard.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-performance-view',
  templateUrl: './performance-view.component.html',
  styleUrls: ['./performance-view.component.scss']
})
export class PerformanceViewComponent implements OnInit {

  mapData: any;
  dashboardService: DashboardService;
  lineChartData: any;
  performanceChartData: any;
  selectedIndicator: any;
  sectors: any;
  selectedSectorId: any;
  selectedSource: any;
  selectedArea: any;
  legends:any[]=[];
  timperiodOfMapData:string=''
  // selectedSector: any;

  constructor(private dashboardProvider: DashboardService, private http: HttpClient) {
    this.dashboardService = dashboardProvider;
   }

  ngOnInit() {
   
    this.getIndicatorSources();
    // this.getThematicMapDataOnLoad();
    // this.getLinechartDataOnLoad();
    // this.getPerformanceChartOnLoad();
    // this.getSectors();
  }

  // getThematicMapDataOnLoad() {
  //   this.dashboardService.getThematicDataOnLoad().subscribe(res => {
  //     this.mapData = res['dataCollection'];
  //     this.selectedArea = {areaCode: 'IND033',
  //     areaName: 'Tamilnadu',
  //     areaNid: 1};
  //     this.legends=res.legends;
  //     this.timperiodOfMapData=res.timePeriod
  //   });
  // }

  getThematicMapDataBySource(sourceId) {
    this.dashboardService.getThematicData(sourceId).subscribe(res => {
      this.mapData = res['dataCollection'];
      this.legends=res.legends;
      this.timperiodOfMapData=res.timePeriod
    });
  }

  getIndicatorSources() {
    this.dashboardService.getIndicatorSources().subscribe(res => {
      let temp: any = res;
      this.dashboardService.sources = temp.reverse();
      if (this.dashboardService.sources) {
        this.selectedSource = this.dashboardService.sources.filter(source => source.sourceId === 27).length?this.dashboardService.sources.filter(source => source.sourceId === 27)[0]:this.dashboardService.sources[0];
        this.selectSource(this.selectedSource)
      }

    });
  }

  // getLinechartDataOnLoad() {
  //   this.dashboardService.getMLineChartDataOnLoad().subscribe(res => {
  //     this.lineChartData = res;
  //   });
  // }

  getLinechartDataOnSelection(areaCode, sourceId) {
    this.dashboardService.getMLineChartDataOnSelection(areaCode, sourceId).subscribe(res => {
      this.lineChartData = res;
    });
  }

  // getPerformanceChartOnLoad() {
  //   this.dashboardService.getPerformanceChartDataOnLoad().subscribe(res => {
  //     this.performanceChartData = res;
  //     this.selectedIndicator = this.dashboardService.getKeys(this.performanceChartData.indicatorData)[0];
  //     // this.http.get("assets/json/bar-dummy.json").subscribe(res => {
  //     //   this.performanceChartData = res;
  //     // })
  //   })
  // }

  getPerformanceChartOnSelection(areaCode, sectorId, sourceId) {
    this.dashboardService.getPerformanceChartDataOnSelection(areaCode, sectorId, sourceId).subscribe(res => {
      this.performanceChartData = res;
      this.selectedIndicator = this.dashboardService.getKeys(this.performanceChartData.indicatorData)[0];
      // this.http.get("assets/json/bar-dummy.json").subscribe(res => {
      //   this.performanceChartData = res;
      // })
    })
  }

  getSectors(){
    this.dashboardService.getIndexIndicatorsOnLoad().subscribe(res => {
      this.sectors = res;
      this.selectedSectorId = this.sectors[0].subsectorKey;
      // this.getThematicMapDataBySource(this.selectedSource.sourceId);
    });
  }

  getSectorsByArea(areaCode, sourceId){
    this.dashboardService.getIndexIndicatorsByArea(areaCode, sourceId).subscribe(res => {
      this.sectors = res;
      if (this.sectors.length) {
        this.selectedSectorId = this.sectors[0].subsectorKey;
        this.getPerformanceChartOnSelection(this.selectedArea.areaCode, this.selectedSectorId, this.selectedSource.sourceId)
      }
      
      // this.getThematicMapDataBySource(this.selectedSource.sourceId);
    });
  }


  selectSource(source) {
    this.selectedSource = source;
    this.selectedArea = {areaCode: 'IND033',
      areaName: 'Tamilnadu',
      areaNid: 1};
    this.getThematicMapDataBySource(this.selectedSource.sourceId);
    this.getSectorsByArea(this.selectedArea.areaCode, this.selectedSource.sourceId);
    this.getLinechartDataOnSelection(this.selectedArea.areaCode, this.selectedSource.sourceId);
  }


  selectSector(selectedSector) {
    // this.selectedSector = this.sectors.filter(sector => sector.key === sectorId)[0];
    this.getPerformanceChartOnSelection(this.selectedArea.areaCode, this.selectedSectorId, this.selectedSource.sourceId);
  }

  selectArea($event){
    this.selectedArea = $event.selectedArea;
    this.getSectorsByArea(this.selectedArea.areaCode, this.selectedSource.sourceId);
    this.getLinechartDataOnSelection(this.selectedArea.areaCode, this.selectedSource.sourceId);
  }

  removeOverAll(sectors){
    return sectors.filter(sector => sector.key !== 'Index - Overall');
  }

  getSectorBySectorId(id){
    return this.sectors.filter(sector => sector.subsectorKey !== id)[0];
  }

  
}
