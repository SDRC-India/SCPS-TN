import { Injectable } from '@angular/core';
import { Observable, Subject, Subscription} from 'rxjs';
import { Cookie } from 'ng2-cookies';

@Injectable()
export class SessionCheckService {


  public timeoutExpired: Subject<number> = new Subject<number>();
  
  constructor() { 

 
    this.timeoutExpired.subscribe(() => {
        Cookie.delete('access_token');
        Cookie.delete('JSESSIONID');
        Cookie.delete('XSRF-TOKEN');
    });

    // this.startTimer();
  }

//   public startTimer() {
//     if (this.timerSubscription) {
//         this.timerSubscription.unsubscribe();
//     }
 
//     this.timer = Observable.timer(this._timeoutSeconds * 1000);
//     this.timerSubscription = this.timer.subscribe(n => {
//         this.timerComplete(n);
//     });
//   }
  
//   public stopTimer() {
//       this.timerSubscription.unsubscribe();
//   }
  
//   public resetTimer() {
//       if (this.timerSubscription) {
//           this.timerSubscription.unsubscribe();
//       }
  
//       this.timer = Observable.timer(this._timeoutSeconds * 1000);
//       this.timerSubscription = this.timer.subscribe(n => {
//           this.timerComplete(n);
//       });
//   }

//   private timerComplete(n: number) {
//     this.timeoutExpired.next(++this._count);

//     if (this.resetOnTrigger) {
//         this.startTimer();
//     }
//   }
}
