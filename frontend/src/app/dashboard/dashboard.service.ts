import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../constants';
import { Indicator } from '../models/indicator';
import { Observable } from 'rxjs';
import { Timeperiod } from '../models/timeformats';
import { Sector } from '../models/sector';
import { Source } from '../models/sources';
import { Data } from '../models/data';

@Injectable()
export class DashboardService {

  isMapLoading: boolean;
  sources: any;
  constructor(private httpClient: HttpClient) { }


  getThematicDataOnLoad() {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getMapDataOnInitialLoad');
  }

  getThematicData(sourceId) {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getMapDataBySource?sourceId=' + sourceId);
  }

  getIndexIndicatorsOnLoad(){
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getIndexIndicatorValuesOnLoad');
  }

  getIndexIndicatorsByArea(areaCode, sourceId){
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getIndexIndicatorValues?areaCode=' + areaCode + '&sourceId=' + sourceId);
  }

  getIndicatorSources() {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getIndicatorSources');
  }

  getMLineChartDataOnLoad() {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getMLineSeriesOnInitialLoad');
    // return this.httpClient.get<any>('assets/json/linechart-dummy.json');
  }

  getMLineChartDataOnSelection(areaCode, sourceId) {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getMLineSeries?areaCode=' + areaCode + '&sourceId=' + sourceId);
    // return this.httpClient.get<any>('assets/json/linechart-dummy.json');
  }

  getPerformanceChartDataOnLoad() {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getPerformanceChartByAreaAndSectorAndSourceOnInitialLoad');
  }

  getPerformanceChartDataOnSelection(areaCode, sectorId, sourceId) {
    return this.httpClient.get<any>(Constants.PERFORMANCE_URL + 'getPerformanceChartByAreaAndSectorAndSource?areaCode=' + areaCode + '&sectorId=' + sectorId + '&sourceId=' + sourceId);
  }


  getKeys(obj){
    return Object.keys(obj);
  }

  isEqual(value, other) {

    // Get the value type
    const type = Object.prototype.toString.call(value);

    // If the two objects are not the same type, return false
    if (type !== Object.prototype.toString.call(other)){ return false; }

    // If items are not an object or array, return false
    if (['[object Array]', '[object Object]'].indexOf(type) < 0) { return false;}

    // Compare the length of the length of the two items
    const valueLen = type === '[object Array]' ? value.length : Object.keys(value).length;
    let otherLen = type === '[object Array]' ? other.length : Object.keys(other).length;
    if (valueLen !== otherLen) return false;

    // Compare two items
    var compare = (item1, item2) => {

      // Get the object type
      var itemType = Object.prototype.toString.call(item1);

      // If an object or array, compare recursively
      if (['[object Array]', '[object Object]'].indexOf(itemType) >= 0) {
        if (!this.isEqual(item1, item2)) return false;
      }

      // Otherwise, do a simple comparison
      else {

        // If the two items are not the same type, return false
        if (itemType !== Object.prototype.toString.call(item2)) return false;

        // Else if it's a function, convert to a string and compare
        // Otherwise, just compare
        if (itemType === '[object Function]') {
          if (item1.toString() !== item2.toString()) return false;
        } else {
          if (item1 !== item2) return false;
        }

      }
    };

    // Compare properties
    if (type === '[object Array]') {
      for (var i = 0; i < valueLen; i++) {
        if (compare(value[i], other[i]) === false) return false;
      }
    } else {
      for (var key in value) {
        if (value.hasOwnProperty(key)) {
          if (compare(value[key], other[key]) === false){ return false;};
        }
      }
    }

    // If nothing failed, return true
    return true;

  };


  getChartDetails(areaId,tabKey,sourceId){
    return this.httpClient.get(Constants.HOME_URL+'bypass/api/lineData?areaNid='+areaId+'&iusNid='+tabKey +'&sourceNid=' + sourceId.key);
  }



getSectors(): Observable<Sector[]>{
  return this.httpClient.get<Sector[]>(Constants.HOME_URL+"bypass/api/sectors");
}
getIndicators(sector:Sector) : Observable<Indicator[]>{
  return this.httpClient.get<Indicator[]>(Constants.HOME_URL+"bypass/api/indicators"+"?sector="+sector.key)
}

getSources(indicator:Indicator): Observable<Source[]>{
  return this.httpClient.get<Source[]>(Constants.HOME_URL+"bypass/api/sources"+"?iusnid="+indicator.description)
}
getTimeperiod(indicator:Indicator,source:Source): Observable<Timeperiod[]>{
  return this.httpClient.get<Timeperiod[]>(Constants.HOME_URL+"bypass/api/timeperiod"+"?iusnid="+indicator.description +"&sourceNid="+source.key)
}

getData(indicator:Indicator,source:Source,areaId,timeperiodId): Observable<Data[]>{
  // indicatorId=2&areaId=IND033&sourceNid=19&timeperiodId=12&childLevel=3
  return this.httpClient.get<Data[]>(Constants.HOME_URL+"bypass/api/data"+"?indicatorId="+indicator.description +"&sourceNid="+
  source.key
  +"&areaId="+areaId
  +"&"+"timeperiodId="
  +timeperiodId
  +"&childLevel=3")
}

exportLineChart(serverURL,svgs)
{
return this.httpClient.post<String>(Constants.HOME_URL+serverURL,svgs).toPromise();
}
exportPdf(serverURL,svgs)
{
return this.httpClient.post<String>(Constants.HOME_URL+serverURL,svgs).toPromise();
}
}
