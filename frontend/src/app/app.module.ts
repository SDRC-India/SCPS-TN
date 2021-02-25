import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './fragments/header/header.component';
import { FooterComponent } from './fragments/footer/footer.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { XhrInterceptorService } from './service/xhr-interceptor.service';
import { AppService } from './app.service';
import { UserService } from './service/user/user.service';
import { AuthGuard } from './guard/auth.guard';
import { SessionCheckService } from './service/session-check.service';
import { HomeComponent } from './home/home.component';
import { Exception404Component } from './exception404/exception404.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule } from '@angular/material'
import { StaticHomeService } from './service/static-home.service';
import { UserManagementModule } from './user-management/user-management.module';
import { RoleGuardGuard } from './guard/role-guard.guard';
import { LoggedinGuard } from './guard/loggedin.guard';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {SdrcLoaderModule} from 'sdrc-loader';
import { DatacollectionModule } from './datacollection/datacollection.module';
import { CommonsEngineProvider } from './datacollection/engine/commons-engine';
import { EngineUtilsProvider } from './datacollection/engine/engine-utils.service';
import { DataSharingServiceProvider } from './datacollection/engine/data-sharing-service/data-sharing-service';
import { MessageServiceProvider } from './datacollection/engine/message-service/message-service';
import { DatePipe } from '@angular/common';
import { ToastModule } from 'ng6-toastr';
import { PipesModule } from './datacollection/engine/pipes/pipes.module';
import { ForgotpassComponent } from './forgotpass/forgotpass.component';
import { XhrInterceptor } from './xhrInterceptor';
@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    Exception404Component,
    LoginComponent,
    ForgotpassComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    HttpClientModule,
    UserManagementModule,
    DatacollectionModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,ToastModule.forRoot(),
    SdrcLoaderModule,
    PipesModule,
    MatIconModule
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: XhrInterceptor, multi: true }, AppService, UserService, AuthGuard, 
    RoleGuardGuard, LoggedinGuard, SessionCheckService, XhrInterceptor,XhrInterceptorService, StaticHomeService,CommonsEngineProvider,DataSharingServiceProvider,EngineUtilsProvider,MessageServiceProvider,DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
