import { Component, OnInit } from '@angular/core';
import { IndexViewServiceService } from '../services/index-view-service.service';
import { SectorFilterPipe } from '../pipes/sector-filter.pipe';
import { saveAs } from '@progress/kendo-file-saver';
import { AreaFilterPipe } from '../pipes/area-filter.pipe';

@Component({
  selector: 'app-index-view',
  templateUrl: './index-view.component.html',
  styleUrls: ['./index-view.component.scss']
})
export class IndexViewComponent implements OnInit {


  selectedStateId: number;
  selectedDistrictsId: number[]=[];
  selectedTimeperiodId: number;
  selectedSectorId: number;

  timeperiodList: any[]=[];
  areaList: any[]=[];
  sectorSubsectorList: any[]=[];
  indicatorList: any[]=[];
  sectorData: any;
  sectorTableData: SectorDataTableModel[] = [];
  indicatorData: any;
  indicatorTableData: any[] = [];
  sectorTableDataColumns: string[]=[];
  indicatorTableDataColumns: string[]=[];
  indicatorTableHeadColumns: string[]=[];
  multiSelectAreaLabel:string='All'
  areaFilterPipe=new AreaFilterPipe();

  indexList: any[];
  tableData = [{
    name: "xyz",
    age: 0,
    action: [{
      "controlType": "button",
      "type": "submit",
      "value": "Edit",
      "class": "btn btn-submit",
      "icon": "fa-edit"
    }]
  }]

  tableColumns = ["name", "age", "action"]

  constructor(private indexViewService: IndexViewServiceService) { }

  ngOnInit() {
    this.getPrefetchData();
  }

  getPrefetchData() {
    this.indexViewService.getPrefetchData().subscribe(res => {
      this.areaList = res['AreaList'];
      this.sectorSubsectorList = res['sectorList'];
      this.indicatorList = res['indicatorList'];
      this.timeperiodList = res['TimePeriod'];

      this.selectedStateId = new AreaFilterPipe().transform(this.areaList, 2, 1)[0].area_NId;
      this.selectedDistrictsId = [0];
      this.checkAllSelection();
      this.selectedTimeperiodId = this.timeperiodList[0].timePeriod_NId;
      this.selectedSectorId = new SectorFilterPipe().transform(this.sectorSubsectorList, -1)[0].ic_NId;
      this.indexList = res['indexList'];
      this.getNewIndexTable()
    })
  }
  validateAllDistrictSelected(){
    let allDistricts = new AreaFilterPipe().transform(this.areaList, 3, this.selectedStateId)
    let selectedDistId = JSON.parse(JSON.stringify(this.selectedDistrictsId));
    if (selectedDistId.length == allDistricts.length && selectedDistId.indexOf(0) == -1) {
        selectedDistId.push(0);
    }
    if (selectedDistId.length <= allDistricts.length && selectedDistId.indexOf(0) != -1) {
        selectedDistId.splice(selectedDistId.indexOf(0), 1);
    }
    this.selectedDistrictsId = selectedDistId;
  }
  checkAllSelection(){
    let allDistricts = new AreaFilterPipe().transform(this.areaList, 3, this.selectedStateId)
    let selectedDistId = JSON.parse(JSON.stringify(this.selectedDistrictsId));
    allDistricts.forEach(district => {
      if (selectedDistId.indexOf(district.area_NId) == -1) {
        selectedDistId.push(district.area_NId);
      }
    });
    this.selectedDistrictsId = selectedDistId;
  }
  checkUncheckAllSelection() {
    setTimeout(() => {
      if (this.selectedDistrictsId.indexOf(0) != -1) {
        this.checkAllSelection();
      }
      else{
        this.selectedDistrictsId = [];
      }
      
    }, 10)


  }


  setSectorTable(data) {
    let tempSectorTableData: SectorDataTableModel[] = [];
    data.forEach(el => {
      let element: SectorDataTableModel = {
        Districts: "",
        "Results for Children": "",
        "ICPS Structure and Functionality": "",
        "Human Resource": "",
        Overall: ""
      };
      element.Districts =el.districtInfo? el.districtInfo.value:"-"
      element["Results for Children"] = el.sectorData.ResultsforChildren;
      element["Human Resource"] = el.sectorData.HumanResources;
      element["ICPS Structure and Functionality"] = el.sectorData.ICPSStructuresandFunctionalities;
      element.Overall = el.sectorData.Overall;
      // if(el.districtInfo)
      tempSectorTableData.push(element);
    });
    if(data[0].districtInfo)
    {
    this.sectorTableData = tempSectorTableData;
    }
    else
    {
    this.sectorTableData =[];
    }
    this.sectorTableDataColumns = Object.keys(tempSectorTableData[0]);
  }
  checkIndicatorHeadRow(headRow, indicator) {
    for (let i = 0; i < headRow.length; i++) {
      const rowObject = headRow[i];
      if (rowObject.value == indicator.split("_")[1]) {
        return i;
      }
    }
    return 0;
  }

  setIndicatorTable(data:any) {
    let tempIndicatorTableData = [];
    this.indicatorTableDataColumns=[];
    this.indicatorTableHeadColumns=[];
    this.indicatorTableData=[];
    let headRow: any[] = [{ value: 'Districts', rowspan: 2 }]

    let tempd ;
    let dataSorted=  Object.keys(data[0].sectorData)
    dataSorted=dataSorted.sort((sector1,sector2)=>{
        if(sector1.split("_")[1]>sector2.split("_")[1])
        {
          return 1;
        }

        else if(sector1.split("_")[1]<sector2.split("_")[1])
        {
          return -1;
        }
        else {
          return 0;
        }
      })

      this.indicatorTableDataColumns.push('Districts')
      dataSorted.forEach(elemet=>{
          if (this.checkIndicatorHeadRow(headRow, elemet)) {
            let i = this.checkIndicatorHeadRow(headRow, elemet);
            headRow[i].colspan = headRow[i].colspan + 1;
          }
          else {
            headRow.push({ value: elemet.split("_")[1], colspan: 1 });
          }
          this.indicatorTableDataColumns.push(elemet.split("_")[0])
      });

    data.forEach((el) => {

      let element = {}
      element['Districts'] = el.districtInfo? el.districtInfo.value:""
      Object.keys(el.sectorData).forEach(key => {
        //.split("_")[0]
        element[key.split("_")[0]] = el.sectorData[key];
   

      })
      // if(el.districtInfo)
      tempIndicatorTableData.push(element);

    });

      if(data[0].districtInfo)
      this.indicatorTableData = tempIndicatorTableData;
      else
      this.indicatorTableData = [];

     
      this.indicatorTableHeadColumns = headRow;


  }

  getAreaById(areaList, areaId) {
    for (let i = 0; i < areaList.length; i++) {
      const area = areaList[i];
      if (area.area_NId == areaId) {
        return area;
      }
    }
    return "";
  }

  getTimeperiodById(timeperiodList, timePeriod_NId) {
    for (let i = 0; i < timeperiodList.length; i++) {
      const timeperiod = timeperiodList[i];
      if (timeperiod.timePeriod_NId == timePeriod_NId) {
        return timeperiod;
      }
    }
    return "";
  }

  getSectorById(sectorSubsectorList, sectorId) {
    for (let i = 0; i < sectorSubsectorList.length; i++) {
      const sector = sectorSubsectorList[i];
      if (sector.ic_NId == sectorId) {
        return sector;
      }
    }
    return "";
  }

  getNewIndexTable() {
    let selectedState = this.getAreaById(this.areaList, this.selectedStateId);
    let selectedDistrictList = [];
    this.selectedDistrictsId.forEach(districtId => {
      if (districtId != 0)
        selectedDistrictList.push(this.getAreaById(this.areaList, districtId))
    });
    let selectedTimeperiod = this.getTimeperiodById(this.timeperiodList, this.selectedTimeperiodId)
    let selectedSector = this.getSectorById(this.sectorSubsectorList, this.selectedSectorId);
    let dataObject = {
      state: selectedState,
      districtList: selectedDistrictList,
      timePeriod: selectedTimeperiod,
      sector: selectedSector
    };
    this.sectorData=[];
    this.indicatorData=[];
    this.indexViewService.getIndexTableByData(dataObject).subscribe(res => {
      // this.loader.hide();
      this.sectorData = res['sectorData'];
      this.indicatorData = res['indicatorData'];
      this.setSectorTable(this.sectorData);
      this.setIndicatorTable(this.indicatorData)
    })


  };


  //export table since we 
  exportTableData(id) {
    let excelName;
    setTimeout(() => {
      var htmls = "";
      var uri = 'data:application/vnd.ms-excel;base64,';
      var template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>';
      var base64 = function (s) {
        return window.btoa(unescape(encodeURIComponent(s)));
      };

      var format = function (s, c) {
        return s.replace(/{(\w+)}/g, function (m, p) {
          return c[p];
        });
      };

      if (id == 'tab1') {
        excelName = "index "+ this.getTimeperiodById(this.timeperiodList, this.selectedTimeperiodId).timePeriod;
        var tab_text = "<div style='font-size:20px;'>Timeperiod : <b style='color:#0054a5; margin-right: 20px'>" + this.getTimeperiodById(this.timeperiodList, this.selectedTimeperiodId).timePeriod;
      }
      else {
        excelName = this.getSectorById(this.sectorSubsectorList, this.selectedSectorId).ic_Name+" "+ this.getTimeperiodById(this.timeperiodList, this.selectedTimeperiodId).timePeriod;
        var tab_text = "<div style='font-size:20px;'>Sector : <b style='color:#0054a5; margin-right: 20px'>" + this.getSectorById(this.sectorSubsectorList, this.selectedSectorId).ic_Name + "</b> Timeperiod : <b style='color:#0054a5; margin-right: 20px'>" +
          this.getTimeperiodById(this.timeperiodList, this.selectedTimeperiodId).timePeriod;
      }
      tab_text += "<table border='2px'>";
      var textRange; var j = 0;
      let tab = document.getElementById(id); // id of table

      for (j = 0; j < tab['rows'].length; j++) {
        if (j == 0 || (id == 'tab2' && j == 1))
          tab_text = tab_text + "<tr style='background-color: #00837F; color: #FFF; font-weight: bold' valign='top'>" + tab['rows'][j].innerHTML + "</tr>";
        else
          tab_text = tab_text + "<tr valign='top'>" + tab['rows'][j].innerHTML + "</tr>";
        //tab_text=tab_text+"</tr>";
      }

      tab_text = tab_text + "</table>";
      tab_text = tab_text.replace(/<A[^>]*>|<\/A>/g, "");//remove if u want links in your table
      tab_text = tab_text.replace(/<img[^>]*>/gi, ""); // remove if u want images in your table
      tab_text = tab_text.replace(/<input[^>]*>|<\/input>/gi, ""); // reomves input params


      var ctx = {
        worksheet: 'Worksheet',
        table: tab_text
      };
      saveAs(new Blob([tab_text], { type: "application/vnd.ms-excel" }), excelName + ".xls");
    }, 200)
  };


  getMatTrigger()
  {

    let areas=this.areaFilterPipe.transform(this.areaList,3,this.selectedStateId)
    if(this.selectedDistrictsId&& this.selectedDistrictsId.length>0)
    {
        if(this.selectedDistrictsId.length>areas.length)
        this.multiSelectAreaLabel='All';

        else if(this.selectedDistrictsId.length==1)
        this.multiSelectAreaLabel = this.areaList.filter(d=>d.area_NId==this.selectedDistrictsId[0])[0].area_Name

        else
        this.multiSelectAreaLabel = this.selectedDistrictsId.length +' Selected';
    }
    else
    this.multiSelectAreaLabel='';
  }
}
