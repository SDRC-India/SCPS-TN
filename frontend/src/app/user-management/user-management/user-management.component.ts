import { Component, OnInit, HostListener } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../../constants';
import { UserManagementService } from '../services/user-management.service';
declare var $: any;

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {  
  form: FormGroup;
  formFields: any;
  sdrcForm: FormGroup;
  
  payLoad = '';
  
  natAreaDetails: any;
  stateList: any;
  parentAreaId: number;
  paramModal: any;
  validationMsg: any;
  btnDisable: boolean = false;
  UserForm:FormGroup;
  firstFieldVariable:any;
  selectedRoleId: number;
  selectedStateId: number;
  selectedDistrictId:number;
  selectedFacilityId: number;
  facilities: any;

  fullName: string;
  userName: string;
  password: string;
  mobile: number;
  email:string;
  usernameRegex = /^[_ A-Za-z0-9]+(?:[A-Za-z0-9]+)*$/;
  passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*_ ])[a-zA-Z0-9!@#$%^&*_ ]/;

  userManagementService: UserManagementService;
  
  constructor(private http: HttpClient, private userManagementProvider: UserManagementService) {
    this.userManagementService = userManagementProvider;
   }

  ngOnInit() {    
    
      this.userManagementService.getUserRoles().subscribe(data=>{
        this.userManagementService.formFieldsAll = data;      
      }) 
    if(!this.userManagementService.areaDetails)   
      this.userManagementService.getAreaDetails().subscribe(data=>{
        this.userManagementService.areaDetails = data;      
      })         
    
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }

  getFacilities(){
    this.userManagementService.getFacilityByRoleAndDistrict(this.selectedRoleId, this.selectedDistrictId).subscribe(res=>{
      this.facilities = res;
    })
  }
 
  submitForm(roleId:any, form: NgForm){ 
    
     let userDetails = {
      "userName":this.userName,
      "password":this.password,
      "designationIds":[roleId],
      "facilityId": this.selectedFacilityId ? this.selectedFacilityId: null,
      "mblNo":this.mobile,
      "email":this.email,
      "areaId":this.selectedDistrictId,
      "name":this.fullName,
      "allowDuplicateMail": true
     }
     this.http.post(Constants.HOME_URL+'createUser', userDetails).subscribe((data) => {
       this.validationMsg = data;    
        $("#successMatch").modal('show');       
        form.resetForm();
     }, err=>{
      $("#oldPassNotMatch").modal('show');
      if(err.status == 409){
        this.validationMsg = "Email Id or Username already taken, try another"
      }
      else
        this.validationMsg = "Some server error occured"

    });
  }
  successModal(){
    $("#successMatch").modal('hide');
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
    $('body,html').click(function(e){   
      if((window.innerWidth)<= 767){
      if(e.target.className == "mob-left-list" || $(e.target).closest(".left-list").length){
        return;
      } else{ 
          // $(".left-list").attr("style", "display: none !important"); 
          // $('.mob-left-list').attr("style", "display: block !important");  
      }
     }
    });  
  }

}
