import { Component, OnInit, ElementRef, Input, ViewChild } from '@angular/core';
import * as d3 from 'd3v4';
// import * as topojson from 'topojson';
// import { NgxSpinnerService } from 'ngx-spinner';
import { Sector } from '../../models/sector';
import { Indicator } from '../../models/indicator';
import { Source } from '../../models/sources';
import { Timeperiod } from '../../models/timeformats';
declare var $: any;

import * as html2canvas from 'html2canvas';
import { Constants } from '../../constants';
import { DashboardService } from '../dashboard.service';
import { AppType } from 'src/app/models/app-type';
import { MatTable } from '@angular/material';

@Component({
  selector: 'app-thematic-dashboard',
  templateUrl: './thematic-dashboard.component.html',
  styleUrls: ['./thematic-dashboard.component.scss']
})
export class ThematicDashboardComponent implements OnInit {

  searchSector='';
  searchIndicator='';
  searchTimePeriod='';
  width;
  height;
  projection;
  path;
  svg;
  g: any;
  mapContainerDiv;

  thematicData: any;
  thematicDropDownList: any;
  legendData: any;
  ngContentId:any;

  mapData:Map<string,any>=new Map();
  mapNameData: any;
  thematicKeys: any;
  clicks: number = 0;
  myDate = new Date();

  dashboardServices: DashboardService;
  lineChartData: any;
  lineChartVisible:boolean=false;
  tabKeys: any;
  areaId: string;
  indicatorName:string;
  indicatorValue:string;
  sideAreaName: string ='Odisha';
  areaName:string;
  primary_url:any;
  isBackBtnClicked:boolean=false;
  areaLevelId:number;

  selectedAreaFromMap :any

	// select the first user of the list
	selectedMapAreaType = "District";

	selectedGranularity =null;
	selectedChildAreaLevel = 3;
	isTrendVisible = true;
  selectedArea = [];
  allIndicators=[];
	show = false;
	shouldDrilldown = true;
	disablePdf = false;
	shoulddisappear=true;
	isColumnVisible = false;
	isLineVisible = false;
	// primary_url = "";
	query = "";
  mapUrl = "";
  
  fetchedSectors:Sector[] = []
  sectors:Sector[] = []
  subsectors:Sector[] = []
  selectedParentSector:Sector = null
  indicators:Indicator[] = []
  sources:Source[] = []
  timeformats:Timeperiod[] = []
  selectedIndicatorType:AppType=AppType.APP_V_2;
  appV2IndicatorType:AppType=AppType.APP_V_2;
  legacyIndicatorType:AppType=AppType.LEGACY;
 
  selectedIndicator:Indicator = new Indicator() ;
  selectedSubsector:Sector = new Sector()
  selectedTimeperiod:Timeperiod = new Timeperiod()
  selectedSource:Source = new Source()
  isActualIndicator:boolean=false;

  footNote:string=Constants.INDEX_COMPUTE_FOOT_NOTE
  // @ViewChild('sectorGroup') sectorGroup;

  dataSource = []
  displayedColumns: string[] = [ 'District', 'Percent', 'Rank'];

  position:number = 0
  @ViewChild(MatTable) table: MatTable<any>;


  constructor( private dashboardService: DashboardService) {
    this.dashboardServices = dashboardService;
   }

  ngOnInit() {
    // this.mapContainerDiv = document.getElementById("map");
    // this.width = this.mapContainerDiv.offsetWidth; 
    // this.height = (window.innerHeight / 2) + 100;

    this.areaId = 'IND033';
    // this.dashboardService.getThematicMapLegends().subscribe(legendData =>{
    //   this.legendData = legendData;
    // })   
   
     this.dashboardService.getSectors().subscribe(sectorData =>{
      this.fetchedSectors = sectorData


       for(let sector of this.fetchedSectors){
            if(sector.description == -1){
                this.sectors.push(sector)
            }
       }
      //  this.selectParentSector(this.sectors[0])
         });
        

  }

  changeIndicatorType(appType)
  {
    this.selectedIndicatorType=appType;
    if(this.isActualIndicator)
    {
    if(this.selectedIndicatorType==AppType.APP_V_2)
    this.indicators=this.allIndicators.filter(d=>d.type==this.selectedIndicatorType && d.classification == 'PERFORMANCE');
    else
    this.indicators=this.allIndicators.filter(d=>d.type==this.selectedIndicatorType);
    }
    else
    this.indicators=this.allIndicators
    
    this.selectedTimeperiod=new Timeperiod();
    this.selectedIndicator=null;
    this.timeformats=[];
    this.thematicData=[];
    this.mapData=new Map(); 
    this.legendData=[];

    if(this.indicators.length)
    {
    this.selectedIndicator = this.indicators[0]
    this.dataSource = []
    // this.table.renderRows()
    this.selectIndicator(this.selectedIndicator)
    }
  }


  selectParentSector(parentSector)
	{
    this.allIndicators=[];
    this.indicators = []
    this.subsectors = []
    this.selectedTimeperiod=new Timeperiod();
    this.thematicData=[];
    this.legendData=[];
    this.thematicKeys=[];
    this.mapData=new Map(); 
    this.selectedSubsector = new Sector()
    this.selectedIndicator = new Indicator()

    for(let sector of this.fetchedSectors){
      if(sector.description == parentSector.key ){
          this.subsectors.push(sector)
      }
 }  
    this.selectedSubsector = this.subsectors[0]
    this.selectSector(this.selectedSubsector)


  }
  
  selectSector(selectedSector){
    this.indicators = []
    this.allIndicators=[];
    this.thematicData=[];
    this.selectedTimeperiod=new Timeperiod();
    this.legendData=[];
    this.thematicKeys=[];
    this.mapData=new Map(); 
     this.dashboardService.getIndicators(selectedSector).subscribe(indi=>{
      this.allIndicators = indi;
     this.isActualIndicator= this.allIndicators.findIndex(d=> d.groupName === 'ACTUAL_INDICATOR') < 0?false:true;
      this.changeIndicatorType(this.selectedIndicatorType)

      // this.selectedIndicator = this.indicators[0]
      // this.selectIndicator( this.selectedIndicator)
    })
  }

  selectIndicator(indicator:Indicator){
    this.selectedIndicator = indicator
    this.indicatorName = indicator.value
      this.dashboardService.getSources(indicator).subscribe(sources=>{
        this.sources = sources

        if(this.sources.length>1)
        this.selectedSource = this.sources.filter(d=>d.key=="27")[0]
        else
        this.selectedSource = this.sources[0]
        this.selectedTimeperiod=new Timeperiod();
        this.timeformats=[];
        this.thematicData=[];
        this.mapData=new Map(); 
        this.legendData=[];
        this.dashboardService.getTimeperiod(this.selectedIndicator, this.selectedSource).subscribe(timeperiods=>{
            this.timeformats = timeperiods
            if(this.timeformats.length)
            {
            this.selectedTimeperiod = this.timeformats[0]
            this.selectTimeperiod(this.selectedTimeperiod)
            }
            
        })
      })
  }

  selectTimeperiod(timeformats:Timeperiod){
    this.selectedTimeperiod = timeformats
    this.thematicData = []
    this.legendData=[];
    this.dataSource = []
    // this.table.renderRows()
    this.mapData=new Map(); 
    this.dashboardService.getData(this.selectedIndicator, this.selectedSource,"IND033",timeformats.key).subscribe(datas=>{
   
      this.thematicData = datas
      this.dataSource = this.thematicData.tableModel
      // this.table.renderRows()
      this.mapLoad("IND033"+".json",this.thematicData)
    })
  }


  mapLoad(primary_url,data){
      this.legendData = this.thematicData;
      this.thematicKeys = Object.keys(this.thematicData);     
      this.mapData=new Map(); 
       this.thematicData.dataCollection.forEach(element => {
        this.mapData.set(element.areaCode,element)
       });
      this.lineChartVisible=false;
      this.areaLevelId =  this.thematicData.dataCollection[Object.keys( this.thematicData.dataCollection)[0]].areaLevelId;
  }



  clickHandler(d){

        this.clicked(d);      

  } 

  clicked(d){
    let selectedArea = d.selectedArea;
    this.selectedAreaFromMap = selectedArea
    this.areaName = selectedArea.areaName;   
    var areaClickId = selectedArea.areaNid;
    this.dashboardService.getChartDetails(areaClickId, this.selectedIndicator.description,this.selectedSource).subscribe(data=>{
      this.lineChartData = data; 
      if(data!=null || data!=undefined){
        this.lineChartVisible=true;
      }
    })      
  }

  closeViz(){
    this.lineChartVisible=false;
  }
  backToMap(){
    this.isBackBtnClicked=true;
    this.areaId = 'IND033';
   // this.mapLoad(this.areaId,this.mapNameData,'odisha_map.json');
    // this.tabListData(this.areaId,this.mapNameData);
    this.sideAreaName='Tamilnadu';
  }


  download(url, data, method) {
		// url and data options required
		if (url && data) {
			// data can be string of parameters or array/object
			data = typeof data == 'string' ? data : $.param(data);
			// split params into form inputs
			var inputs = '';
			$.each(data.split('&'), function() {
				var pair = this.split('=');
				inputs += '<input type="hidden" name="' + pair[0] + '" value="'	+ pair[1] + '" />';
			});
			// send request
			$(
					'<form action="' + url + '" method="' + (method || 'post')
							+ '">' + inputs + '</form>').appendTo('body')
					.submit().remove();
			$(".loader").css("display", "none");
		}
	}


  sdrc_export(){
    // d3.selectAll("svg").attr("version", 1.1).attr("xmlns", "http://www.w3.org/2000/svg");
			
    d3.select("#linechart").selectAll("path").attr("style",function(d) {
      return  "fill:"+ $(this).css('fill')+";stroke:"+$(this).css('stroke')+";stroke-width:"+$(this).css('stroke-width');
    });

    d3.select("#linechart").selectAll("circle").attr("style",function(d) {
      return  "fill:"+ $(this).css('fill')+";stroke:"+$(this).css('stroke');
    });

    d3.select("#linechart").selectAll("text").attr("style",function(d) {
      return  "fill:"+ $(this).css('fill')
    });

    d3.select("#linechart").selectAll(".tick").attr("style",function(d) {
      return  "color:"+ $(this).css('color')
    });

    d3.select("#linechart").selectAll("line").attr("style",function(d) {
      return  "stroke:"+ $(this).css('stroke')
    });

    // var trendSvg = $("#linechart").html();
    //d3.select("#trendsvg").selectAll("path").attr('style', null);
    $('#lineChartClose').css('display', "none");
    $('#trendPdfButton').css('display', "none");
    $('#pdfDownloadBtnForIndex').css('display', "none");
      
      html2canvas(document.querySelector("#lineChartId")).then(canvas => {
      let	lineChartBox = canvas.toDataURL('image/png');
                  $('#lineChartClose').css('display', "block");
                  $('#trendPdfButton').css('display', "block");
                  $('#pdfDownloadBtnForIndex').css('display', "block");
                 var serverURL='bypass/exportLineChart?iusNid=' + this.selectedIndicator.description
                  + '&areaNid=' + this.selectedAreaFromMap.areaNid+'&sourceNid=' + this.selectedSource.key
                  var svgs=[];
                 svgs.push(lineChartBox);
                 svgs.push(this.selectedAreaFromMap.areaName);
                 svgs.push(this.selectedIndicator.value);
                //  svgs.push(trendSvg);
                 svgs.push($(window).width());
        this.dashboardService.exportLineChart(serverURL,svgs).then(respone =>{
            if(respone["File"] ==null || respone["File"].trim()=="")
            {
           //  $scope.formError="Something Went Wrong";
            }
          else
         {	 
           $('#linechart').css('display', "");
           $('#trendPdfButton').css('display', "");
         var fileName = {"fileName" :respone["File"]};
         this.download(Constants.HOME_URL+"bypass/downloadPDF", fileName, 'POST');
         }
          }).catch(
          error =>
          {
            $('#linechart').css('display', "");
            $('#trendPdfButton').css('display', "");
          }
          )
              
    });


  }


  downloadMap(mapData,selectedIndicator,source,timeperiod){

   
            // $('html, body').animate({
            //     scrollTop: $('#legendsection').offset().top
            // }, 1000);
                {

              //  $('#pdfDownloadBtn').html('<i class="fa fa-lg fa-download"></i> Download PDF <img src="resources/images/preloader.gif" />');
                d3.selectAll("svg").attr("version", 1.1).attr("xmlns", "http://www.w3.org/2000/svg");
                
                d3.select("#map svg").selectAll("path").attr("style",function(d) {
                  return  "fill:"+ $(this).css('fill')+";stroke:"+$(this).css('stroke')+";background-color:"+$(this).css('background-color');
                });
         
              

                let samikshaMapg = d3.select('#map').html().replace(/\&nbsp;/g, " ");
                
                  // var legend
                  // var topBottom
                  // var topBottomContainer = $("#tbsection");
                  // var legendContainer = $("#legendsection");
                  var svgs=[] ;
                  // html2canvas(document.querySelector("#tbsection")).then(tbsection=>{


                    {

                      // temp code
                      svgs.push("No top bottom");
                    //   svgs.push(tbsection.toDataURL('image/png', 1.0));
                    //  var topBottom = tbsection.toDataURL('image/png', 1.0);
           
                      
                      html2canvas(document.querySelector("#legendsection")).then(legendSection=>
                          {
                          
               
                            //  var legend = canvas.toDataURL('image/png', 1.0);
                              
                              svgs.push(legendSection.toDataURL('image/png', 1.0));
                              
                              svgs.push(samikshaMapg);
                              
                              
                              svgs.push($(window).width());
                              
                              var serverUrl="bypass/api/exportPDF?indicatorId="+selectedIndicator.description+
                              "&areaId=IND033&sourceNid="+source.key+
                              "&timeperiodId="+timeperiod.key+"&childLevel=3";
                            
                            
                              this.dashboardService.exportLineChart(serverUrl,svgs).then(respone =>{
                                if(respone["File"] ==null || respone["File"].trim()==""){

                     

                                }else{
                                
                                var fileName = {"fileName" :respone["File"]};
                                this.download(Constants.HOME_URL+"bypass/downloadPDF", fileName, 'POST');
                    
                                }}
                                ).catch(
                                  error =>
                                  {
                           
                                  }
                                  )
                            
                          });
                    
         
              }

                  // })
                
                
                
        
    };
  }

}
