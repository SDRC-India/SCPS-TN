import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../constants';

declare var $: any;

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  reportForms: any;
  allArea: any;
  constructor(private httpClient: HttpClient) { }

   /**
   * Get all report forms
   */
  getAllReportForms() {
    return this.httpClient.get(Constants.HOME_URL + 'getReportForms');
  }

  getAllArea() {
    return this.httpClient.get(Constants.HOME_URL + 'getAllArea');
  }

  /**
   * Download the report excel
   * @param data
   */
  download(data) {
    if (data) {
      // data can be string of parameters or array/object
      data = typeof data === 'string' ? data : $.param(data);
      // split params into form inputs
      let inputs = '';
      const url = Constants.HOME_URL + 'downloadReport';
      $.each(data.split('&'), function () {
        const pair = this.split('=');
        inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
      });
      // send request
      $('<form action="' + url + '" method="post">' + inputs + '</form>')
        .appendTo('body').submit().remove();
    }
  }
}
