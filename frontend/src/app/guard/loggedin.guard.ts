import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import {Location} from '@angular/common';
import { AppService } from '../app.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedinGuard implements CanActivate {
  constructor(private app: AppService, private router: Router, private _location: Location) { } 

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
      
      if(!this.app.checkLoggedIn()){
        return true;
      }else{
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
}
