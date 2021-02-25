import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse  } from '@angular/common/http';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { catchError,  } from 'rxjs/operators';
import { Constants } from './constants';
import { AppService } from './app.service';


@Injectable() 
export class XhrInterceptor implements HttpInterceptor 
{ 
  constructor( private router: Router,private app:AppService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const xhr = req.clone({ headers: req.headers.set('X-Requested-With', 'XMLHttpRequest') }); 
    if(req.url != Constants.HOME_URL + 'user')
    {
    return next.handle(xhr)
    .pipe(catchError(error => {
        if (error instanceof HttpErrorResponse) {
            switch ((<HttpErrorResponse>error).status) {
              // case 400:
              //   return this.handle400Error(error);
              case 401:
                return this.handle401Error(req, next,error);

              case 403:
              return this.handle403Error(req, next,error); 
            }
            return throwError(error);
          } else {
            return throwError(error);
          }
    }));
  }
  else
  {
    return next.handle(xhr);
  }
} 

handle403Error(req: HttpRequest<any>, next: HttpHandler, error:HttpErrorResponse){

  this.app.errorCode=error.status;
  this.app.errorMessage="You are not authorized to view this page !"
  this.router.navigateByUrl("exception")
  return throwError("");
}

handle401Error(req: HttpRequest<any>, next: HttpHandler, error:HttpErrorResponse){

    this.app.errorCode=error.status;
    this.app.errorMessage="Your session has been expired.Please login again !"

    
    if(this.app.checkLoggedIn())
    {
      sessionStorage.removeItem('userDetails');
      sessionStorage.removeItem('credentials');
      sessionStorage.removeItem("dataDontEntryAllowRoute");
    this.router.navigateByUrl("exception")
    }
    else
    {
      sessionStorage.removeItem('userDetails');
      sessionStorage.removeItem('credentials');
      sessionStorage.removeItem("dataDontEntryAllowRoute");
    this.router.navigateByUrl("")
    }


    return throwError("");
}

}