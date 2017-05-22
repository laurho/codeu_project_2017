import { Component } from '@angular/core';

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
  }


  constructor (private http: Http) {}



  //text = this.getHeroes().subscribe(data => this.text1 = data);;




  signUpUser(): String {
    // this.http.post('http://localhost:8080/myapp/wechat/adduser', {"name": 'Hobby', "password": "pass"})
    //          .map(this.extractData)
    //          .subscribe(data => console.log(data));

    this.http.post('http://localhost:8080/myapp/wechat/adduser', JSON.stringify({"username": this.model.name, "password": this.model.password }))
             .map(this.extractData)
             .subscribe(data => console.log(data));

    // return this.http.get('http://localhost:8080/myapp/wechat/adduser').map(this.extractData);
    this.submitted = true; 

    return "user created";


    // post(url: string, body: any, options?: RequestOptionsArgs) : Observable<Response>
  }



  // getHeroes(): Observable<string> {
  //   console.log("function called");

  //   return this.http.get('http://localhost:8080/myapp/wechat/testtext')
  //                   .map(this.extractData);
  //                   // .catch(this.handleError);
  // }
  private extractData(res: Response) {
    // let body = res.json();
    // return body.data || { };

    console.log("helloo");
    return res.text();//["hi"];
  }

}