import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Route, Router } from '@angular/router';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import { AppService } from '../app.service';
import { FormService } from '../service/form-service.service';

@Injectable({
  providedIn: 'root'
})
export class DataEntryRouteGuardGuard implements CanActivate {
 
  constructor(private app: AppService, private router: Router, public formService: FormService) { } 

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
     return this.app.checkDataEntryDoneForMonth().map(flag=>{
        if(this.formService.getDraftData() || !flag){
        return true; 
      }else{ 
       this.router.navigateByUrl("/drafts");
       return false
      } 
      });
     
  }
}
