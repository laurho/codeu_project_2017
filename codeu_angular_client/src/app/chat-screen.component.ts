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
      .interval(1000 * 3)
      .subscribe(x => {
        this.retrieveAllConvos();
    });

    Observable
      .interval(1000 * 1)
      .subscribe(x => {
        this.retrieveAllCurrentMessages();
    });


  }


  allConvos : Array<Object> = [];

  allMsgs : Array<Object> = [];


  /* Retrieves all the joinable conversations and 
   * saves the resulting JSON list to allConvos
   */
  private retrieveAllConvos() {
    
    // Send post request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/showallconvos')
             .map(this.extractData)
             .subscribe(data => {
               this.allConvos = data;
               this.cd.detectChanges();
             });
  }

  // Converts the provided response into JSON
  private extractData(res: Response) {
    let body = res.json();
    return body;
  }

  /* 
   * Selects the current conversation
   * id - String Uuid of the conversation to select
   */
  private selectConvo(id: String) {

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/selectconvo', id)
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllCurrentMessages();
              });
  }


  /* 
   * Retrieves all messages from the current conversation
   */
  private retrieveAllCurrentMessages() {
    
    // Send post request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/showcurrentmessages')
             .map(this.extractData)
             .subscribe(data => {
               this.allMsgs = data;
               this.cd.detectChanges();
             });
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

  private createNewConvo(convoTitle: String) {
    console.log(convoTitle);

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/createnewconvo', convoTitle)
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllConvos();
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