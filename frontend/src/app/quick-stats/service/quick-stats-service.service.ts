import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Constants } from 'src/app/constants';

@Injectable({
  providedIn: 'root'
})
export class QuickStatsServiceService {

  constructor(private httpClient:HttpClient) { }

  getQuickStatData():Observable<any>
  {
    return this.httpClient.get(Constants.HOME_URL+"bypass/getQuickStats")
  }
}
