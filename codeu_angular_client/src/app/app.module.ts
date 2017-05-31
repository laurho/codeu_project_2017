/*Defines AppModule, the root module that tells 
Angular how to assemble the application. Right 
now it declares only the AppComponent. Soon there 
will be more components to declare.*/

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }	 from '@angular/forms';
import { HttpModule, JsonpModule } from '@angular/http';


import { AppComponent }  from './app.component';

import { LoginFormComponent } from './login-form.component';
import { ChatScreenComponent } from './chat-screen.component';

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
  	
    LoginFormComponent,
    ChatScreenComponent
  ],
  providers: [
  	
  ],
  bootstrap:    [ AppComponent ]
})
export class AppModule { }


