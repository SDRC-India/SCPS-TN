import { Component, OnInit, Input } from '@angular/core';
import { Router, RouterState, NavigationEnd } from '@angular/router';
import { StaticHomeService } from '../service/static-home.service';
import { AppService } from '../app.service';
declare var $: any;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  
  whatnew:string = "What's New";
  homeHindiDetails : any[] = [];
  appService: AppService;
  staticService: StaticHomeService;
  private twitter: any;
  constructor(private app: AppService, private staticServiceProvider: StaticHomeService, private router: Router) {
    this.initTwitterWidget();
    this.appService = app;
    this.staticService = staticServiceProvider;
  
   }

   initTwitterWidget() {
    this.twitter = this.router.events.subscribe(val => {
      if (val instanceof NavigationEnd) {
        (<any>window).twttr = (function (d, s, id) {
          let js: any, fjs = d.getElementsByTagName(s)[0],
              t = (<any>window).twttr || {};
          if (d.getElementById(id)) return t;
          js = d.createElement(s);
          js.id = id;
          js.src = "https://platform.twitter.com/widgets.js";
          fjs.parentNode.insertBefore(js, fjs);

          t._e = [];
          t.ready = function (f: any) {
              t._e.push(f);
          };

          return t;
        }(document, "script", "twitter-wjs"));

        if ((<any>window).twttr.ready())
          (<any>window).twttr.widgets.load();

      }
    });
  }

  ngOnInit(){
     this.staticService.getData("Home").subscribe(data=>{
      this.staticService.reinitializeData(data);
      
    })

    // this.rotateNumber();
    var a=0;
    $(window).scroll(function(){
      var oTop = $('.count').offset().top - window.innerHeight;
      if (a == 0 && $(window).scrollTop() > oTop){
        $('.count').each(function () {
          $(this).prop('Counter',0).animate({
              Counter: $(this).text()
          }, {
              duration: 5000,
              easing: 'swing',
              step: function (now) {
                  $(this).text(Math.ceil(now));
              }
          });
      });
      a = 1;
      }
    });
    this.twitter.unsubscribe();
  }

  getNewsContent(item){
    this.staticService.whatsNewCategory = item;
     this.router.navigate(['pages/whatsnewcontent']);
    //window.open('whatsnewcontent');
  }
  // $(window).scroll(function(){})
//   rotateNumber(){
//   $('.count').each(function () {
//     $(this).prop('Counter',0).animate({
//         Counter: $(this).text()
//     }, {
//         duration: 5000,
//         easing: 'swing',
//         step: function (now) {
//             $(this).text(Math.ceil(now));
//         }
//     });
// });
//   }



}