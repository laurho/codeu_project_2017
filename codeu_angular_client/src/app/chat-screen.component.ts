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


    /* Call once at start to ensure the conversation options 
    are loaded by the time the user is logged in */
    this.retrieveAllConvos();


    Observable
      .interval(1000 * 60)
      .subscribe(x => {
        if (AppSettings.clientContextId != null){
          this.retrieveAllConvos();
          console.log("convos updating...")
        }
        
    });

    Observable
      .interval(1000 * 2)
      .subscribe(x => {
        if (AppSettings.clientContextId != null && this.selectedConvo != null){
          this.retrieveAllCurrentMessages();
          console.log("messages updating...")
        }
    });

  }


  allConvos : Array<Object> = [];

  allMsgs : Array<Object> = [];

  selectedConvo : String = null;
  loadingConvo : Boolean = false;


  /* Retrieves all the joinable conversations and 
   * saves the resulting JSON list to allConvos
   */
  private retrieveAllConvos() {
    
    // Send post request
    this.http.post(AppSettings.API_ENDPOINT + 'wechat/showallconvos', AppSettings.clientContextId)
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
   * chosenConvoId - String Uuid of the conversation to select
   */
  private selectConvo(chosenConvoId: String) {

    this.selectedConvo = chosenConvoId
    this.loadingConvo = true;

    let params: any = new Object();
    params.chosenConvo = chosenConvoId;
    params.clientContextId = AppSettings.clientContextId;

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/selectconvo', JSON.stringify(params))
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
    this.http.post(AppSettings.API_ENDPOINT + 'wechat/showcurrentmessages', AppSettings.clientContextId)
             .map(this.extractData)
             .subscribe(data => {
               this.allMsgs = data;
               this.loadingConvo = false;
               this.cd.detectChanges();
             });
  }


  private sendMsg(msgToSend: String) {
    console.log(msgToSend);

    let params: any = new Object();
    params.msgToSend = msgToSend;
    params.clientContextId = AppSettings.clientContextId;

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/sendmsg', JSON.stringify(params))
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllCurrentMessages();
              });
  }

  private createNewConvo(convoTitle: String) {
    console.log(convoTitle);

    let params: any = new Object();
    params.convoTitle = convoTitle;
    params.clientContextId = AppSettings.clientContextId;

    this.http.post(AppSettings.API_ENDPOINT + 'wechat/createnewconvo', JSON.stringify(params))
             .map(this.extractData)
             .subscribe(data => {
                console.log(data);
                this.retrieveAllConvos();
              });
  }


}