import { Injectable } from '@angular/core';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class IndexViewServiceService {
  prefetchedData: any;
  constructor(private http: HttpClient) { }

  getPrefetchData(){
    return this.http.get(Constants.HOME_URL + "bypass/getPrefetchData");
  }
  
  getIndexTableByData(data){
    return this.http.post(Constants.HOME_URL + "bypass/factSheetData", data);
    // return this.http.get("assets/json/sectorTable.json", data);
  }
}
