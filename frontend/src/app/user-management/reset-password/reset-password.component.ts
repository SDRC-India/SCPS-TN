import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http/';
import { UserManagementService } from '../services/user-management.service';
import { Constants } from '../../constants';
import { Router, RoutesRecognized } from '@angular/router';
import { filter, pairwise } from 'rxjs/operators';

declare var $: any;


@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  form: FormGroup;
  formFields: any;
  formFieldsAll: any;
  payLoad = '';
  areaDetails: any;  

  newPassword: string;
  confirmPassword: string;
  userId: any;
  validationMsg: any;
  user: any;
  disableUserId: number;
  passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*_ ])[a-zA-Z0-9!@#$%^&*_ ]/;
 
  userManagementService: UserManagementService;

  constructor(private http: HttpClient, private userManagementProvider: UserManagementService, private router: Router) {
    this.userManagementService = userManagementProvider;
   }

  ngOnInit() {
    this.router.events
    .pipe(filter((e: any) => e instanceof RoutesRecognized))
    .subscribe((e: any) => {
        if(this.router.url =="/reset-password" &&e.url != '/edit-user' ){
          this.userManagementService.resetPasswordDetails ={};
        }
    });
    
    
      this.userManagementService.getAllRoles().subscribe((data)=>{
        let roles:any[]= <any>data
        if(data)
        this.userManagementService.formFieldsAll = roles.filter(d=>d.code != 'SCPS' 
        && d.code != 'COMMISIONER' &&   d.code != 'PO_NZONE' &&  d.code != 'PO_SZONE'
        && d.code != 'PO_STATE_LEVEL'  && d.code != 'CCI');      
      }) 
    if(!this.userManagementService.areaDetails)   
      this.userManagementService.getAreaDetails().subscribe(data=>{
        this.userManagementService.areaDetails = data;      
      })      
                   
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
    if(this.userManagementService.resetPasswordDetails.selectedRoleId)
    this.getUsers();   
  }

 getUsers(){
    this.userManagementService.getUsersByRoleIdAreaId(this.userManagementService.resetPasswordDetails.selectedRoleId, this.userManagementService.resetPasswordDetails.selectedDistrictId).subscribe(res => {
      this.userManagementService.resetPasswordDetails.allUser  = res;
    })
 }
 resetModal(user){
   $("#resetPassModal").modal('show');
  this.user = user;
 }
 resetBox(user){
  this.newPassword = "";
  this.confirmPassword = "";
 }
 submitModal(form:NgForm){   

  let passDetails = {
    'userId' : this.user.userId,
    'newPassword': this.newPassword
  };

 if(this.newPassword === this.confirmPassword) {
    this.http.post(Constants.HOME_URL + 'resetPassword', passDetails).subscribe((data)=>{  
        $("#resetPassModal").modal('hide');
        $("#successMatch").modal('show');
        this.newPassword = "";
        this.confirmPassword = "";
        this.userManagementService.resetPasswordDetails.allUser = undefined;      
    }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg ="Error occurred";
    });
  }
}
editUserDetails(data){
  this.userManagementService.editUserDetails = data;  
  this.router.navigateByUrl("edit-user");
}
enableUser(id){
  this.http.get(Constants.HOME_URL + 'enableUser?userId='+id).subscribe((data)=>{
    $("#enableUser").modal('show'); 
    this.validationMsg = data;
  }, err=>{      
  }); 
}
disableUser(id){
  this.disableUserId = id;
  $("#disableUserModal").modal('show');
}
disableUserDetails(id){
  this.http.get(Constants.HOME_URL +'disableUser?userId='+id).subscribe((data)=>{   
   $("#disableUserModal").modal('hide');
   $("#enableUser").modal('show'); 
     this.validationMsg = data;        
   }, err=>{      
     console.log(err);       
     $("#disableUserModal").modal('hide');     
   }); 
}
userStatus(){
  $("#enableUser").modal('hide'); 
  this.getUsers();
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
      if(e.target.className == "mob-left-list"){
        return;
      } else{ 
          $(".left-list").attr("style", "display: none !important"); 
          $('.mob-left-list').attr("style", "display: block !important");  
      }
     }
    });   
  }

}
