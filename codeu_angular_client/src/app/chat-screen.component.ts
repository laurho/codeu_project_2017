import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';

import { User }    from './user';
import { AppSettings } from './appSettings';

import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';


@Component({
  selector: 'chat-screen',
  templateUrl: './chat-screen.component.html'
})
export class ChatScreenComponent {

  // We will need access to these imports in the code
  constructor (
    private http: Http, 
    private cd: ChangeDetectorRef) {}


  /* This is tied to the inputs of the form; 
     Changing either the form's inputs' text or the fields of 
       'model' will change the other dynamically */
  model = new User('', '');


  allConvos : Array<Object> = [];


  /* Retrieves all the joinable conversations
  */
  private retrieveAllConvos(): String {
    
    // Send post request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/showallconvos')
             .map(this.extractData)
             .subscribe(data => {
               this.allConvos = data;
               this.cd.detectChanges();

               console.log(this.allConvos);
             });

    return "convos retrieved";
  }

  // Converts the provided response into JSON
  private extractData(res: Response) {
    let body = res.json();


    return body;
  }































  





  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }


}