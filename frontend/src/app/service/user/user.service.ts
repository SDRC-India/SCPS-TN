import { Injectable } from '@angular/core';
import { Cookie } from 'ng2-cookies'
import { Router } from '@angular/router'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Constants } from '../../constants';

@Injectable()
export class UserService {
  constructor(private router: Router, private http: HttpClient) { }

  checkLoggedIn() : boolean{
    if (!Cookie.check('access_token')){
        return false
    }else{
      return true
    }
  }
  

  logout() {
    Cookie.delete('access_token');
    this.router.navigate(['/login']);
  }


  obtainAccessToken(loginData){

    const httpOptions = {
      headers: new HttpHeaders({ 'Authorization': 'Basic ' + btoa("client:clientpassword"), 
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8' }, )
    };

 

    this.http.post(Constants.HOME_URL + 'user', httpOptions).subscribe(
      data=>{       
     
        this.saveToken(data)
      },

      err=>{
        console.log(err)
      }
    )     
  }



  // obtainAccessTokenFromRefreshToken(){

  //   const httpOptions = {
  //     headers: new HttpHeaders({ 'Authorization': 'Basic ' + btoa("client:clientpassword"), 
  //     'Content-type': 'application/x-www-form-urlencoded; charset=utf-8' }, )
  //   };

  //   let params = new URLSearchParams();
  //   params.append('refresh_token',"a93dd1f1-3c36-4ae8-ba8b-9935496f7cab");
  //   params.append('grant_type','refresh_token');

  //   this.http.post('/oauth/token', params.toString(), httpOptions).subscribe(
  //     data=>{        
  //       // this.saveToken(data)
  //       console.log("access_token : ", data)
  //     },

  //     err=>{
  //       console.log(err)
  //     }
  //   )     
  // }


  saveToken(token){
    var expireDate = new Date().getTime() + (1000 * token.expires_in);
    let date = new Date(expireDate);

    console.log("expireDate "+ date)
    Cookie.set("access_token", token.access_token, expireDate);
    this.router.navigate(['/']);
  }
  

}
