/*Defines the same AppComponent as the one 
in the QuickStart playground. It is the root 
component of what will become a tree of nested 
components as the application evolves.*/

import { Component } from '@angular/core';
import { Hero } from './hero';
import { HeroService } from './hero.service';


import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';



@Component({
  selector: 'my-app',
  template: `
    <br><br><br>
    <login-form></login-form>

    <!--
    <br><br><br><br><br><br>
    <h1>{{text1}}</h1>

    <input #usrnm placeholder="User Name" />
    <input #pswd placeholder="Password" type="password" />
    <button type="submit" (click)="createAccount()" >Create Account</button>
    <br>
    <br>


  	<nav>
       <a routerLink="/dashboard">Dashboard</a>
       <a routerLink="/heroes">Heroes</a>
    </nav>
    
    

  	<router-outlet></router-outlet>
    -->

  	`,
  providers: [HeroService],
  styleUrls: ['./app.component.css']
})
export class AppComponent  {
	name = 'Angular';
	title = 'Tour of Rubans';
  text1 = '';
  text = this.getHeroes().subscribe(data => this.text1 = data);;


  constructor (private http: Http) {}

  getHeroes(): Observable<string> {
    console.log("function called");

    return this.http.get('http://localhost:8080/myapp/wechat/testtext')
                    .map(this.extractData);
                    // .catch(this.handleError);
  }
  private extractData(res: Response) {
    // let body = res.json();
    // return body.data || { };

    console.log("helloo");
    console.log(res.text())
    return res.text();//["hi"];
  }

  private createAccount(){
    
    // console.log(usrnm.value);
    console.log("create account function called!");
  }

  // private handleError (error: Response | any) {
  //   // In a real world app, you might use a remote logging infrastructure
  //   let errMsg: string;
  //   if (error instanceof Response) {
  //     const body = error.json() || '';
  //     const err = body.error || JSON.stringify(body);
  //     errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
  //   } else {
  //     errMsg = error.message ? error.message : error.toString();
  //   }
  //   console.error(errMsg);
  //   return Observable.throw(errMsg);
  // }

	
}


