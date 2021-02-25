import { Component, OnInit } from '@angular/core';
import { AppService } from '../app.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Constants } from '../constants';
import { ToastsManager } from 'ng6-toastr';
declare var $: any;
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  credentials: any = {};
  otpSendError: boolean = false;
  forgotpassEmailId: string;
  forgotpassOTP: string;
  otpSentMessage: any;
  updateMessage:any;
  otpSent: boolean = false;;
  otpVerified: boolean = false;
  newpassword: string;
  passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*_ ])[a-zA-Z0-9!@#$%^&*_ ]/;
  usernameRegex = /^[_ A-Za-z0-9]+(?:[A-Za-z0-9]+)*$/;


  constructor(public app:AppService, private router: Router, private http: HttpClient, public toastr: ToastsManager) {

   }

  ngOnInit() {
  }

  login(){
     this.app.authenticate(this.credentials, () => {
      if(this.app.authenticated === true){
        if(this.app.getUserDetails()){
          const userDetails = this.app.getUserDetails();
          if (userDetails.designations[0] === 'ADMIN'){
            this.router.navigateByUrl('/user-management');
        }else if (userDetails.designations[0] === 'SCPS' || userDetails.designations[0] === 'Commisioner' 
		      || userDetails.designations[0] === 'PO STATE LEVEL'){
          this.router.navigateByUrl('/dashboard/performance-view');
       }else if(userDetails.designations[0] === 'PO North Zone'
       || userDetails.designations[0] === 'PO South Zone'){
        this.router.navigateByUrl('/dashboard/thematic-view');
       }
         else {
            this.app.checkDataEntryDoneForMonth().subscribe(resp=>{
              this.app.setDataDontEntryAllowRoute(resp)
              this.router.navigateByUrl('/drafts');
            });
          }
      }
      }
      else{
        $(".error-message").fadeIn("slow");
        setTimeout(function(){
          $(".error-message").fadeOut("slow");
        }, 5000)
      }
    });
    return false;
  }

  checkDataEntryDoneForMonth(){
    this.app.checkDataEntryDoneForMonth();
  }

  clearInput(){
    this.forgotpassEmailId = '';
    this.forgotpassOTP = '';
    this.otpSent = false;
    this.otpSentMessage = '';
  }

  sendOTP(emailId){
    this.otpSent = false;
    this.http.get(Constants.HOME_URL + "bypass/sendOtp?userName="+emailId + "&valueType=uName" ).subscribe(res => {
      this.otpSent = true;
      this.otpSentMessage = res;
    }, err => {
      this.otpSentMessage = err.error;
    })
  }

  isValidOtp(){
      if(this.forgotpassOTP && this.forgotpassOTP.length==6){
        this.http.get(Constants.HOME_URL + "bypass/validateOtp?userName="+this.forgotpassEmailId + "&valueType=uName" + "&otp=" + this.forgotpassOTP ).subscribe(res => {
          this.otpSent = true;
          this.otpSentMessage = res;
        }, err => {
          this.otpSentMessage = err.error;
        })
      }
  }

  setPass(){
    let data = {
      emailId: this.forgotpassEmailId,
      otp: this.forgotpassOTP,
      newPassword: this.newpassword,
      confirmPassword: this.newpassword,
      valueType: 'uName'
    }
    this.http.post(Constants.HOME_URL + "bypass/forgotPassword", data).subscribe(res => {
      this.updateMessage = res;
    },err=>{
      this.updateMessage = err.error;
    })
  }
}
