import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';

import { User }    from './user';
import { AppSettings } from './appSettings';

import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
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
    private cd: ChangeDetectorRef) {


    this.retrieveAllConvos();

    Observable
      .interval(1000 * 30)
      .subscribe(x => {
        // console.log("updating things");
        this.retrieveAllConvos();
    });


  }


  /* This is tied to the inputs of the form; 
     Changing either the form's inputs' text or the fields of 
       'model' will change the other dynamically */
  model = new User('', '');


  allConvos : Array<Object> = [];

  allMsgs : Array<Object> = [];

  data : Observable<Array<Object>>;

  
  


  /* Retrieves all the joinable conversations
  */
  private retrieveAllConvos(): String {
    
    // Send post request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/showallconvos')
             .map(this.extractData)
             .subscribe(data => {
               this.allConvos = data;
               this.cd.detectChanges();

               // console.log(this.allConvos);
             });

    return "convos retrieved";
  }

  // Converts the provided response into JSON
  private extractData(res: Response) {
    let body = res.json();


    return body;
  }


  private selectConvo(id: String) {
    console.log(id);

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/selectconvo', id)
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllCurrentMessages();
              });
  }


  private retrieveAllCurrentMessages(): String {
    
    // Send post request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/showcurrentmessages')
             .map(this.extractData)
             .subscribe(data => {
               this.allMsgs = data;
               this.cd.detectChanges();

               console.log(this.allMsgs);
             });

    return "messages retrieved";
  }


  private sendMsg(msgContents: String) {
    console.log(msgContents);

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/sendmsg', msgContents)
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllCurrentMessages();
              });
  }


  // private init() {
  //   this.data = this.getlst();

  //   this.data.forEach(
  //       value => this.allConvos.push(value)
  //   );
  // }


  // private getlst(): Observable<Array<Object>> {
    
  //   // Send post request
  //   return this.http.get(AppSettings.API_ENDPOINT + 'wechat/showallconvos')
  //            .map(this.extractData);
  // }



























  





  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }


}