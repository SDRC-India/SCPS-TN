import { Component, OnInit } from '@angular/core';
import { Route, Router } from '@angular/router';
import { AppService } from '../../app.service';
declare var $:any;
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  user = null;
  app: AppService;
  router: Router;
  constructor(private appProvider: AppService, private routerProvider: Router){
    this.app = appProvider;
    this.router = routerProvider;
  }

  ngOnInit() {
    $(window).scroll(() => {
      const sticky = $('.sticky');
      const scroll = $(window).scrollTop();
      if (scroll >= 100){
        sticky.addClass('navbar-fixed');
      } else{
        sticky.removeClass('navbar-fixed');
      }
    });
    
  }


}
