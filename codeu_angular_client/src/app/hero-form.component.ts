import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';

import { Hero }    from './hero';


import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';



@Component({
  selector: 'hero-form',
  templateUrl: './hero-form.component.html'
})
export class HeroFormComponent {




  topErrorMessage : String = null;


  powers = ['Really Smart', 'Super Flexible',
            'Super Hot', 'Weather Changer'];

  model = new Hero(18, 'Dr IQ', 'bleep', this.powers[0], 'Chuck Overstreet');

  submitted = false;

  onSubmit() { 
  	this.submitted = true; 
  }

  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }

  newHero() {
  	this.model = new Hero(42, '', '', '');
    this.topErrorMessage = null;
  }


  constructor (private http: Http, private cd: ChangeDetectorRef) {}

  ref = this.cd;

  //text = this.getHeroes().subscribe(data => this.text1 = data);;




  private signUpUser(): String {
    // this.http.post('http://localhost:8080/myapp/wechat/adduser', {"name": 'Hobby', "password": "pass"})
    //          .map(this.extractData)
    //          .subscribe(data => console.log(data));

    console.log(this.submitted);
    console.log(this.http);
    console.log(this.cd);


    let param: any = new Object();
    param.username = this.model.name;
    param.password = this.model.password;

    this.http.post('http://localhost:8080/myapp/wechat/adduser', JSON.stringify(param))
             .map(this.extractData)
             .subscribe(data => {
                if (data.error != undefined) {
                  // There was some error
                  this.topErrorMessage = data.error;
                } else {
                  // Successful signup
                  console.log("inside message");

                  this.submitted = true;

                }

                this.cd.detectChanges();
              });

    // return this.http.get('http://localhost:8080/myapp/wechat/adduser').map(this.extractData);
    // this.submitted = true; 

    return "user created";


    // post(url: string, body: any, options?: RequestOptionsArgs) : Observable<Response>
  }




  private signInUser(): String {
    
    // TODO: replace param with this.model for cleaner code

    let param: any = new Object();
    param.username = this.model.name;
    param.password = this.model.password;

    this.http.post('http://localhost:8080/myapp/wechat/signinuser', JSON.stringify(param))
             .map(this.extractData)
             .subscribe(data => {
                if (data.error != undefined) {
                  // There was some error
                  this.topErrorMessage = data.error;
                } else {
                  // Successful signup
                  console.log("inside message");

                  this.submitted = true;

                }

                this.cd.detectChanges();
              });

    return "user created";


    // post(url: string, body: any, options?: RequestOptionsArgs) : Observable<Response>
  }

  private extractData(res: Response) {
    let body = res.json();

    console.log(this.submitted);
    console.log(this.http);
    console.log(this.cd);

    console.log(body);
    
    return body;
    
  }

  private subscribeData(data: any) {
    console.log(data);
    console.log("inside subscribe");
    console.log(data.error != undefined);
    
    if (data.error != undefined) {
      // There is an error message
      this.topErrorMessage = data.error;
    } else {

      console.log("inside message");

      this.submitted = true;

    }

    console.log(this.submitted);
    console.log(this.http);
    console.log(this.cd);

    this.cd.detectChanges();

    console.log("after ifs");

  }


}