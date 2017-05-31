/*Compiles the application with the JIT
compiler and bootstraps the application's
main module (AppModule) to run in the browser.
The JIT compiler is a reasonable choice during
the development of most projects and it's the
only viable choice for a sample running in a
live-coding environment like Plunker. You'll
learn about alternative compiling and deployment
 options later in the documentation. */
"use strict";
var platform_browser_dynamic_1 = require("@angular/platform-browser-dynamic");
var app_module_1 = require("./app/app.module");
platform_browser_dynamic_1.platformBrowserDynamic().bootstrapModule(app_module_1.AppModule);
//# sourceMappingURL=main.js.map