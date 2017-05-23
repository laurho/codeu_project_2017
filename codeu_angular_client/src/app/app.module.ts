/*Defines AppModule, the root module that tells 
Angular how to assemble the application. Right 
now it declares only the AppComponent. Soon there 
will be more components to declare.*/

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }	 from '@angular/forms';
import { HttpModule, JsonpModule } from '@angular/http';


import { AppComponent }  from './app.component';
import { HeroDetailComponent } from './hero-detail.component';
import { HeroesComponent } from './heroes.component';
import { HeroService }         from './hero.service';
import { DashboardComponent } from './dashboard.component';
import { LoginFormComponent } from './login-form.component';

import { AppRoutingModule }     from './app-routing.module';

@NgModule({
  imports:      [ 
  	BrowserModule,
  	FormsModule,
  	AppRoutingModule,
    HttpModule,
    JsonpModule
  ],
  declarations: [ 
  	AppComponent,
  	DashboardComponent,
  	HeroDetailComponent,
  	HeroesComponent,
    LoginFormComponent
  ],
  providers: [
  	HeroService
  ],
  bootstrap:    [ AppComponent ]
})
export class AppModule { }


