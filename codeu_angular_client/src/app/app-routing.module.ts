import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent }   from './login-form.component';


const routes: Routes = [
  { path: '', redirectTo: '/app', pathMatch: 'full' },
  { path: 'app',  component: LoginFormComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})

export class AppRoutingModule {}