import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Constants } from 'src/app/constants';
import { HttpClient } from '@angular/common/http';
import { UserManagementService } from '../services/user-management.service';
import { Router, RoutesRecognized } from '@angular/router';

declare var $: any;

@Component({
  selector: 'app-add-facility',
  templateUrl: './add-facility.component.html',
  styleUrls: ['./add-facility.component.scss']
})
export class AddFacilityComponent implements OnInit {
  validationMsg: any;
  fcname:string;
  fctype:string;
  address:string;
  dist:string;
  weblink:string;
  noh:string;
  contact:string;
  fcemail:string;
  establishment:string;
  expiry:string;
  lastinspection:Date;
  complaint:string;
  boys6:number;
  boys16:number;
  boys11:number;
  boys14:number;
  boysyr18:number;
  boystotal:number;
  girls6:number;
  girls11:number;
  girls12:number;
  girls18:number;
  totalgirls:number;
  cwcwithout:number;
  cwcwithorder:number;
  residing:string;
  remark:string;
  rgno:string;
  sanctioned:number;
  selectedFacilityToDel: any;
  userManagementService: UserManagementService;
  p:number; q: number;
  addFormTemp: NgForm;


  constructor(private http: HttpClient,private userManagementProvider: UserManagementService, private router: Router) {
    this.userManagementService = userManagementProvider;

   }

  ngOnInit() {
    if(!this.userManagementService.areaDetails)   
    this.userManagementService.getAreaDetails().subscribe(data=>{
      this.userManagementService.areaDetails = data;      
    })  
    
    if(!this.userManagementService.facilityType)   
    this.userManagementService.getFacilityType().subscribe(data=>{
      this.userManagementService.facilityType = data;      
    })

    this.userManagementService.getUserRoles().subscribe(data=>{
      this.userManagementService.formFieldsAll = data;      
    }) 
    
    if(this.userManagementService.resetPasswordDetails.selectedRoleId)
    this.getUsers();   

    
    
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }

  }

  
  
  getUsers(){
    this.userManagementService.getFacilityByRoleAndDistrict(this.userManagementService.resetPasswordDetails.selectedRoleId, this.userManagementService.resetPasswordDetails.selectedDistrictId).subscribe(res => {
      this.userManagementService.resetPasswordDetails.allUser  = res;
      if(this.userManagementService.resetPasswordDetails.allUser.length) {
        this.userManagementService.resetPasswordDetails.allUser.forEach(el => {
          el.establishmentDate = el.establishmentDate;
          el.expiryDate = el.expiryDate;
          el.latestInspectionDate = el.latestInspectionDate;
        });
      }
    })
 }

  getFacilityTypeNameById(id){
    for (let i = 0; i < this.userManagementService.facilityType.length; i++) {
      const el = this.userManagementService.facilityType[i];
      if(el.id == id) {
        return el;
      }
    }
  }

  openVerifyDistrictModal(form: NgForm) {
    this.addFormTemp = form;
    $("#verifyDistrict").modal('show')
  }

  submitForm(form: NgForm){ 
    
    let facilityDetails = {
      "facilityId":null,
     "name":this.fcname,
     "facilityType": this.getFacilityTypeNameById(this.fctype).name,
     "designationId": this.fctype,
     "nameAndAddress":this.address,
     "areaId":this.dist,
     "websiteLink":this.weblink,
     "nameOfHead":this.noh,
     "phNo":this.contact,
     "emailId":this.fcemail,
     "establishmentDate":this.establishment,
     "expiryDate":this.expiry,
     "latestInspectionDate":this.lastinspection,
     "detailsOfComplaintBoxPlaced":this.complaint=="Yes"?true:false,
     "boys0To6":this.boys6,
     "boys7To11":this.boys11,
     "boys12To14":this.boys14,
     "boys15To18":this.boysyr18,
     "boysTotal":this.boystotal,
     "girls0To6":this.girls6,
     "girls7To11":this.girls11,
     "girls12To14":this.girls12,
     "girls15To18":this.girls18,
     "girlsTotal":this.totalgirls,
     "noOfChildrenWithOrdersOfCWC":this.cwcwithorder,
     "noOfChildrenWithoutOrdersOfCWC":this.cwcwithout,
     "reasonsforChildrenResidingInTheCCIWithoutCWCOrder":this.residing,
     "otherRemarks":this.remark,
     "registrationNo":this.rgno,
     "sanctionedStrength":this.sanctioned 
    }
    this.http.post(Constants.HOME_URL+'saveFacility', facilityDetails, {responseType: "text"}).subscribe((data) => {
      this.validationMsg = data;    
       $("#successMatch").modal('show');       
       form.resetForm();
    }, err=>{
     $("#oldPassNotMatch").modal('show');
     if(err.status == 409){
       this.validationMsg = "Username already taken, try another"
     }
     else
       this.validationMsg = "Some server error occured"

   });

  
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

editFacilityDetails(data){
  this.userManagementService.editFacilityDetails = data;  
  this.router.navigateByUrl("edit-facility");
}


confirmInactiveFacility(event, facility) {
  event.preventDefault();
  this.selectedFacilityToDel = facility;
  $("#confirmInactive").modal("show");
}

activateOrDeactivateFacility(facility, active, facilityId) {
  this.userManagementService.changeFacilityStatus(active, facilityId).subscribe(res => {
    facility.active = !active;
  })
}

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
  this.boystotal=(this.boys6 ? Number(this.boys6):0) +(this.boys11 ? Number(this.boys11):0 )+(this.boys14 ? Number(this.boys14):0) +(this.boysyr18 ? Number(this.boysyr18):0);
  }

  sumGirls() {
    this.totalgirls=(this.girls6 ? Number(this.girls6):0) +(this.girls11 ? Number(this.girls11):0 )+(this.girls12 ? Number(this.girls12):0) +(this.girls18 ? Number(this.girls18):0);
    }
}
