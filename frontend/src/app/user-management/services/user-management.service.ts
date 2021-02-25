import { Injectable } from '@angular/core';
import { Constants } from '../../constants';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  formFieldsAll: any;
  typeDetails: any;
  areaDetails: any;  
  facilityType:any;
  allTypes: any;
  editUserDetails: any;
  editFacilityDetails: any;
  resetPasswordDetails: any={};
  facilityNameForSuggestion: any;
  constructor(private httpClient: HttpClient) { }

  getUserRoles(){
    return this.httpClient.get(Constants.HOME_URL + 'getFacilityLevelDesignations');   
  }
  getAllRoles(){
    return this.httpClient.get(Constants.HOME_URL + 'getAllDesignations');   
  }
  getAreaDetails(){
    return this.httpClient.get(Constants.HOME_URL + 'getAllArea');   
  }
  // getTypes(){
  //   return this.httpClient.get(Constants.HOME_URL + 'getTypes');   
  // }
  getUsersByRoleIdAreaId(roleId, areaId){
    return this.httpClient.get(Constants.HOME_URL + 'getUsers?roleId='+roleId+'&areaId='+areaId)
  }
  
  getFacilitiesByDistrictId(areaId){
    return this.httpClient.get(Constants.HOME_URL + 'getFacilities?districtId='+areaId)
  }

  /*facility desgination type*/
  getFacilityType(){
    return this.httpClient.get(Constants.HOME_URL + 'getFacilityLevelDesignations');   
  }

  getFacilityByRoleAndDistrict(roleId, districtId){

    return this.httpClient.get(Constants.HOME_URL + 'getFacilityByRoleAndDistrict?designationId='+roleId+'&districtId='+districtId);   

  }

  changeFacilityStatus(active, id) {
    return this.httpClient.get(Constants.HOME_URL + 'changeFacilityStatus?active='+active+'&facilityId='+id, {responseType: "text"});   
  }

  getFacilityNamesForSuggestion() {
    return this.httpClient.get(Constants.HOME_URL + 'getFacilitiesForSuggestion');   
  }
}
