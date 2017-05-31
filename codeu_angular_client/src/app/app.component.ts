/*Defines the same AppComponent as the one 
in the QuickStart playground. It is the root 
component of what will become a tree of nested 
components as the application evolves.*/

import { Component } from '@angular/core';


import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';



@Component({
  selector: 'my-app',
  template: '<router-outlet></router-outlet>',
  providers: [],
  styleUrls: ['./app.component.css']
})
export class AppComponent  {}


