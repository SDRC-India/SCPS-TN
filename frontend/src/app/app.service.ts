import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router'
import { Constants } from './constants';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

    userName: string = null;
    authenticated = false;
    logoutSuccess:boolean = false;
    errorCode : any = 404;
    errorMessage : String ='Page not found'
   
    constructor(private http: HttpClient, private router: Router) {
    }

    
    authenticate(credentials, callback) {
        const headers = new HttpHeaders(credentials ? {
            authorization : 'Basic ' + btoa(credentials.username + ':' + credentials.password)
        } : {});
        const httpOptions = {
                headers: new HttpHeaders({ 'Authorization': 'Basic ' + btoa(credentials.username + ':' + credentials.password), 
                'Content-type': 'application/x-www-form-urlencoded; charset=utf-8' } )
              };

        this.http.get(Constants.HOME_URL + 'user', httpOptions).subscribe(response => {
            if (response['username']) {
                this.authenticated = true;
                this.saveUserDetails(response)
                sessionStorage.setItem("credentials", JSON.stringify(credentials))
                
            } else {
                this.authenticated = false;
            }
            
            return callback && callback();
        },
        error => {
            if(error.status == 401){
                this.authenticated = false;
                return callback && callback();
            }
        }
    );

    }


    checkLoggedIn() : boolean{
       return !!sessionStorage.getItem("userDetails");
    }
    

    logout() {
      
        this.http.post(Constants.HOME_URL + 'logout',{}).subscribe(response =>{
        
            sessionStorage.removeItem('userDetails');
            sessionStorage.removeItem('credentials');
            sessionStorage.removeItem("dataDontEntryAllowRoute");
            // this.toastr.success("Logged out successfully");
            this.logoutSuccess = true;
            setTimeout(()=>{
                this.logoutSuccess = false;
            },2000)   
            this.router.navigate(['/']);        
        })
    }

    saveUserDetails(details){
        sessionStorage.setItem("userDetails", JSON.stringify(details));
    }
    getUserDetails(){
        return JSON.parse(sessionStorage.getItem("userDetails"));
    }

     //handles nav-links which are going to be shown 
  checkUserAuthorization(expectedRoles) {

    let flag = false;
    if(this.getUserDetails()){
    if (this.checkLoggedIn()) {
      expectedRoles.forEach(expectedRole => {
        for(let i=0; i< this.getUserDetails().authorities.length; i++){          
          if (this.getUserDetails().authorities[i].authority == expectedRole) {
            flag = true;
          }  
        }      
      });      
    }
   }
    return flag;
  }


  checkDataEntryDoneForMonth():Observable<Boolean>{
    return  this.http.get<Boolean>(Constants.API_URL+"checkDataEntryStatus");
  }

  setDataDontEntryAllowRoute(resp){
      sessionStorage.setItem("dataDontEntryAllowRoute",JSON.stringify(resp))
  }

  getDataDontEntryAllowRoute(){
    return JSON.parse(sessionStorage.getItem("dataDontEntryAllowRoute"));
  }


}
