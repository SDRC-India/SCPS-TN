import { Component, OnInit } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormService } from 'src/app/service/form-service.service';
import { ReviewPageModel } from 'src/app/interface/reviewpagemodel';
import { UserService } from 'src/app/service/user/user.service';
import { AppService } from 'src/app/app.service';
import { Router } from '@angular/router';

@Component({ 
  selector: 'app-drafts',
  templateUrl: './drafts.component.html',
  styleUrls: ['./drafts.component.scss']
})
export class DraftsComponent implements OnInit {
  drafts : any = null;
  formId:any;


  constructor(private http:HttpClientModule,private formService : FormService,private userService:UserService,
    private appService: AppService,
    private router:Router
    ) { }

  
  ngOnInit() {
    this.formId =this.appService.getUserDetails()['sessionMap'].assignedFormId[0].form.id
    this.formService.getDrafts( this.formId).subscribe(data=>{
        this.drafts = data
    });
    

  }

  navigate(route:any,mode:any,formData:any){
        formData['mode']= mode
        this.formService.setDraftData(formData)
        this.router.navigateByUrl(route);
  }

  setDataDontEntryAllowRoute(resp){
    sessionStorage.setItem("dataDontEntryAllowRoute",JSON.stringify(resp))
}

getDataDontEntryAllowRoute(){
  return JSON.parse(sessionStorage.getItem("dataDontEntryAllowRoute"));
}

}
