import { Component, OnInit } from '@angular/core';
import { UserManagementService } from '../services/user-management.service';
import { Router, RoutesRecognized } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormGroup } from '@angular/forms';
import { filter } from 'rxjs/operators';
import { Constants } from 'src/app/constants';
declare var $: any;

@Component({
  selector: 'app-edit-facility-details',
  templateUrl: './edit-facility-details.component.html',
  styleUrls: ['./edit-facility-details.component.scss']
})
export class EditFacilityDetailsComponent implements OnInit {
  form: FormGroup;
  formFields: any;
  sdrcForm: FormGroup;
  selectedDistrictId:number;
  validationMsg: any;
  userManagementService: UserManagementService;
  facilities: any;
  selectedFacilityId: number;


  constructor(private http: HttpClient, private userManagementProvider: UserManagementService, private router: Router) { 
    this.userManagementService = userManagementProvider;

  }

  ngOnInit() {
    // this.router.navigateByUrl("add-facility");
    if(!this.userManagementService.editFacilityDetails){
      this.router.navigateByUrl("add-facility");
    }
    else{
      this.selectedDistrictId = this.userManagementService.editFacilityDetails.area.areaId;
      this.selectedFacilityId = this.userManagementService.editFacilityDetails.facilityId;
    }

    this.userManagementService.getFacilityNamesForSuggestion().subscribe(res => {
      this.userManagementService.facilityNameForSuggestion = res;
    })

    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }

  getFacilities(){
    if(this.selectedDistrictId)
    this.userManagementService.getFacilitiesByDistrictId(this.selectedDistrictId).subscribe(res=>{
      this.facilities = res;
      
    })
  }

  successModal(){
    $("#successMatch").modal('hide');
    this.router.navigateByUrl("add-facility");
  }
  convertToExpair(date: string): Date | null{
    if (date)
    return new Date(date);
  else
    return null
  }
  convertToDate(){
    return new Date();

}

  updateFacilityDetails(facilityId:any,form){
    let facilityDetails = {
      "facilityId":facilityId,
     "name":this.userManagementService.editFacilityDetails.name,
     "facilityType": this.getFacilityTypeNameById(this.userManagementService.editFacilityDetails.designation.id).name,
     "designationId": this.userManagementService.editFacilityDetails.designation.id,
     "nameAndAddress":this.userManagementService.editFacilityDetails.nameAndAddress,
     "areaId":this.selectedDistrictId,
     "websiteLink":this.userManagementService.editFacilityDetails.contactDetails.websiteLink,
     "nameOfHead":this.userManagementService.editFacilityDetails.contactDetails.nameOfHead,
     "phNo":this.userManagementService.editFacilityDetails.contactDetails.phNo,
     "emailId":this.userManagementService.editFacilityDetails.contactDetails.emailId,
     "establishmentDate":this.userManagementService.editFacilityDetails.establishmentDate,
     "expiryDate":this.userManagementService.editFacilityDetails.expiryDate,
     "latestInspectionDate":this.userManagementService.editFacilityDetails.latestInspectionDate,
     "detailsOfComplaintBoxPlaced":this.userManagementService.editFacilityDetails.detailsOfComplaintBoxPlaced,
     "boys0To6":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.boys0To6,
     "boys7To11":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.boys7To11,
     "boys12To14":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.boys12To14,
     "boys15To18":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.boys15To18,
     "boysTotal":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.boysTotal,
     "girls0To6":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.girls0To6,
     "girls7To11":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.girls7To11,
     "girls12To14":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.girls12To14,
     "girls15To18":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.girls15To18,
     "girlsTotal":this.userManagementService.editFacilityDetails.numberOfChildrenPresent.girlsTotal,
     "noOfChildrenWithOrdersOfCWC":this.userManagementService.editFacilityDetails.orderDetailsOfCWC.noOfChildrenWithOrdersOfCWC,
     "noOfChildrenWithoutOrdersOfCWC":this.userManagementService.editFacilityDetails.orderDetailsOfCWC.noOfChildrenWithoutOrdersOfCWC,
     "reasonsforChildrenResidingInTheCCIWithoutCWCOrder":this.userManagementService.editFacilityDetails.orderDetailsOfCWC.reasonsforChildrenResidingInTheCCIWithoutCWCOrder,
     "otherRemarks":this.userManagementService.editFacilityDetails.otherRemarks,
     "registrationNo":this.userManagementService.editFacilityDetails.registrationNo,
     "sanctionedStrength":this.userManagementService.editFacilityDetails.sanctionedStrength 
    }

 this.http.post(Constants.HOME_URL+'saveFacility', facilityDetails, {responseType:"text"}).subscribe((data) => {
      this.validationMsg = data;    
       $("#successMatch").modal('show');       
      //  form.resetForm();
    }, err=>{
     $("#oldPassNotMatch").modal('show');
     if(err.status == 409){
       this.validationMsg = "Username already taken, try another"
     }
     else
       this.validationMsg = "Some server error occured"

   });

  }
  getFacilityTypeNameById(id){
    for (let i = 0; i < this.userManagementService.facilityType.length; i++) {
      const el = this.userManagementService.facilityType[i];
      if(el.id == id) {
        return el;
      }
    }
  }

  // formatDate(date) {
  //   return date.toLocaleDateString('en-GB', {
  //     day: 'numeric', month: 'numeric', year: 'numeric'
  //   }).split("/").join(".");
  // }

  // unformatDate(dateString) {
  //   return new Date(dateString.split(".").join("/"));
  // }
  showLists(){    
    $(".left-list").attr("style", "display: block !important"); 
    $('.mob-left-list').attr("style", "display: none !important");
  }

  ngAfterViewInit(){
    $("input, textarea, .select-dropdown").focus(function() {
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#4285F4"})
      
    })
    $("input, textarea, .select-dropdown").blur(function(){
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#333"})
    })
  }

  sumBoys() {
    let childCount = this.userManagementService.editFacilityDetails.numberOfChildrenPresent
    childCount.boysTotal=(childCount.boys0To6 ? Number(childCount.boys0To6):0) +(childCount.boys7To11 ? Number(childCount.boys7To11):0 )+(childCount.boys12To14 ? Number(childCount.boys12To14):0) +(childCount.boys15To18 ? Number(childCount.boys15To18):0);
    }
  
    sumGirls() {
      let childCount = this.userManagementService.editFacilityDetails.numberOfChildrenPresent
      childCount.girlsTotal=(childCount.girls0To6 ? Number(childCount.girls0To6):0) +(childCount.girls7To11 ? Number(childCount.girls7To11):0 )+(childCount.girls12To14 ? Number(childCount.girls12To14):0) +(childCount.girls15To18 ? Number(childCount.girls15To18):0);
      }
}
