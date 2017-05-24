import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';

import { User }    from './user';
import { AppSettings } from './appSettings';

import { Http, Response }          from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';


@Component({
  selector: 'login-form',
  templateUrl: './login-form.component.html'
})
export class LoginFormComponent {

  // We will need access to these imports in the code
  constructor (
    private http: Http, 
    private cd: ChangeDetectorRef) {}

  // When these are not null, text assigned to them will display at the top of the form
  topErrorMessage : String = null;
  topSuccessMessage : String = null;

  /* This is tied to the inputs of the form; 
     Changing either the form's inputs' text or the fields of 
       'model' will change the other dynamically */
  model = new User('', '');

  /* When this is false, the login form is displayed
     When it is true, the user is logged in, and the 
       chat app's contents are displayed */
  submitted = true;

  // Clears the form
  clearForm() {
    this.model = new User('', '');
    this.topErrorMessage = null;
    this.topSuccessMessage = null;
  }

  // Converts the provided response into JSON
  private extractData(res: Response) {
    let body = res.json();    
    return body;
  }


  /* Submits a post request to the server to create a new account.
     Note: it does not actually sign the user in, making it nessesary 
           for the user to sign in afterwards
  */
  private signUpUser(): String {
    
    // Create the object to JSONize and send
    // TODO: this can be replaced by the model for cleaner code
    let param = new User(this.model.username, this.model.password);

    // The form could be cleared at this point, but its easier for the user to not to
    // this.clearForm();

    // Send post request
    this.http.post(AppSettings.API_ENDPOINT + 'wechat/adduser', JSON.stringify(param))
             .map(this.extractData)
             .subscribe(data => {
                if (data.error != undefined) { 
                  // There was some error, so the recieved error message is displayed
                  this.topErrorMessage = data.error;
                } else {
                  // Sign up was successful, so a message is displayed
                  this.topSuccessMessage = data.message;
                  this.topErrorMessage = null;
                }

                /* Because Angular does not check for changes with subscribe functions,
                   we must manually make it check for such changes
                   This is important because divs in the html that are 
                   listening for changes (here, the messages to be 
                   displayed) won't be triggered otherwise */
                this.cd.detectChanges();
              });

    return "user created";
  }


  /* Submits a post request to the server to sign in a user.
  */  
  private signInUser(): String {
    
    // Create the object to JSONize and send
    // TODO: this can be replaced by the model for cleaner code
    let param = new User(this.model.username, this.model.password);

    // Send post request
    this.http.post(AppSettings.API_ENDPOINT + 'wechat/signinuser', JSON.stringify(param))
             .map(this.extractData)
             .subscribe(data => {
                if (data.error != undefined) {
                  // There was some error, so the recieved error message is displayed
                  this.topErrorMessage = data.error;

                  // For user-friendliness, the form's fields are filled in again, rather than being left blank
                  this.model.username = param.username;
                  this.model.password = param.password;

                } else {
                  // Sign in was successful, so we can hide the login page and show the rest of the chat app!
                  this.submitted = true;
                }

                // See explanation provided in 'signUpUser'
                this.cd.detectChanges();
              });

    return "user signed in";
  }


  /* Submits a get request to the server to sign out the current user.
  */  
  private signOutUser(): String {
    
    // Send get request
    this.http.get(AppSettings.API_ENDPOINT + 'wechat/signoutuser')
             .map(this.extractData)
             .subscribe(data => {
                if (data.error != undefined) {
                  // There was some error, so the recieved error message is displayed
                  this.topErrorMessage = data.error;
                  this.submitted = false;

                } else {
                  // Sign out was successful, so we can hide the login page and show the rest of the chat app!
                  this.topSuccessMessage = data.message;
                  this.submitted = false;
                }

                // See explanation provided in 'signUpUser'
                this.cd.detectChanges();
              });

    return "user signed out";
  }



  // TODO: Remove this when we're done
  get diagnostic() { return JSON.stringify(this.model); }


}