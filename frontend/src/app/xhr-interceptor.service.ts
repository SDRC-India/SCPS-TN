import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse, HttpClient, HttpHeaders, HttpSentEvent, HttpHeaderResponse, HttpProgressEvent, HttpResponse, HttpUserEvent } from '@angular/common/http';
import { Observable, of, BehaviorSubject, Subject, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { catchError, tap, switchMap, finalize, filter, take } from 'rxjs/operators';
import { Constants } from './constants';

@Injectable()
export class XhrInterceptorService implements HttpInterceptor {

  isRefreshTokenExpired: boolean;
  isRefreshingLogin: boolean = false;
  tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);

  constructor( private router: Router, private http: HttpClient) { }

  

  intercept(req: HttpRequest<any>, next: HttpHandler) {

    if(req.url != Constants.HOME_URL + 'user')
    {
    return next.handle(req)
      .pipe(catchError(error => {
        if (error instanceof HttpErrorResponse) {
          switch ((<HttpErrorResponse>error).status) {
            // case 400:
            //   return this.handle400Error(error);
            case 401:
              return this.handle401Error(req, next);
          }
          return throwError(error);
        } else {
          return throwError(error);
        }
      }));
    }
    else
    {
      return next.handle(req);
    }
  }

  handle401Error(req: HttpRequest<any>, next: HttpHandler) {
    if(req.url=="/scpstamilnadu/user"){
      this.logoutUser();
    }
    if (!this.isRefreshingLogin) {
      this.isRefreshingLogin = true;

      // Reset here so that the following requests wait until the token
      // comes back from the refreshToken call.
      this.tokenSubject.next(null);
        return this.refreshLogin()
        .pipe(switchMap((res: string) => {
          if (res) {
            this.tokenSubject.next(res);
            this.isRefreshingLogin = false;
            return next.handle(req)
            
          }
          // If we don't get a new token, we are in trouble so logout.
          return this.logoutUser();
        }))
        .pipe(catchError(error => {
          // If there is an exception calling 'refreshToken', bad news so logout.
          return this.logoutUser();
        }))
        .pipe(finalize(() => {
          this.isRefreshingLogin = false;
        }));
      // });
      
    } else {
      return this.tokenSubject
        .pipe(filter(token => token != null))
        .pipe(take(1))
        .pipe(switchMap(token => {
          return next.handle(req);
        }));
    }
  }

  handle400Error(error) {
    if (error && error.status === 400 && error.error && error.error.error === 'invalid_grant') {
      // If we get a 400 and the error message is 'invalid_grant', the token is no longer valid so logout.
      this.logoutUser();
    }

    return Observable.throw(error);
  }

 

  logoutUser() {
    // Route to the login page (implementation up to you)
    sessionStorage.removeItem("userDetails");
    sessionStorage.removeItem("credentials")
    window.location.href = "login"
    return throwError("");
  }

  

  refreshLogin(): Observable<any> {
    const tokenObsr = new Subject<any>();
    const credentials = JSON.parse(sessionStorage.getItem("credentials"));
  
    if (credentials) {
  
        let URL: string = Constants.HOME_URL + 'user'
        const headers = new HttpHeaders(credentials ? {
            authorization : 'Basic ' + btoa(credentials.username + ':' + credentials.password)
        } : {});
        const httpOptions = {
                headers: new HttpHeaders({ 'Authorization': 'Basic ' + btoa(credentials.username + ':' + credentials.password), 
                'Content-type': 'application/x-www-form-urlencoded; charset=utf-8' } )
              };
  
        this.http.post(URL, httpOptions)
            .subscribe(response => {
  
            
  
                tokenObsr.next(response);
  
            }, err => {
                this.logoutUser();
            });
    }
    return tokenObsr.asObservable();
  }
}
