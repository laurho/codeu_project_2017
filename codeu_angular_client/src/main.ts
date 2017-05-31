/*Compiles the application with the JIT 
compiler and bootstraps the application's 
main module (AppModule) to run in the browser. 
The JIT compiler is a reasonable choice during 
the development of most projects and it's the 
only viable choice for a sample running in a 
live-coding environment like Plunker. You'll 
learn about alternative compiling and deployment
 options later in the documentation. */

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

platformBrowserDynamic().bootstrapModule(AppModule);
