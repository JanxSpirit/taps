// Set the require.js configuration for your application.
require.config({
  // Initialize the application with the main application file
  deps: ["main"],

  paths: {
    // JavaScript folders
    libs: "/js/libs",
    plugins: "/js/plugins",

    // Libraries
    jquery: "/js/libs/jquery",
    underscore: "/js/libs/underscore",
    backbone: "/js/libs/backbone",
    backboneforms: "/js/libs/backbone-forms",
    modelbinding: "/js/libs/backbone.modelbinding",
    handlebars: "/js/libs/handlebars-1.0.0.beta.6",
    layoutmanager: "/js/plugins/backbone.layoutmanager",
    bootstrapdropdown: "/js/plugins/bootstrap-dropdown",
    bootstrapdatepicker: "/js/plugins/bootstrap-datepicker",
    base64: "/js/libs/base64",
    gx: "/js/libs/GX",
    modaldialog: "/js/libs/Backbone.ModalDialog",

    // Shim Plugin
    use: "/js/plugins/use"
  },

  use: {
    backbone: {
      deps: ["use!underscore", "jquery"],
      attach: "Backbone"
    },

    backboneforms: {
      deps: ["use!backbone"]
    },

    underscore: {
      attach: "_"
    },

    base64: {
      attach: "Base64"
    },

    layoutmanager: {
      deps: ["use!backbone"]
    },
     handlebars: {
        attach: "Handlebars"
    },
    modelbinding: {
        deps: ["use!backbone", "use!underscore"],
        attach: "ModelBinding"
    },
    bootstrapdropdown: {
        deps: ["jquery"]
    },
    bootstrapdatepicker: {
        deps: ["jquery"]
    },
    gx: {
        deps: ["jquery"]
    },
    modaldialog: {
        deps: ["use!backbone"]
    }
  }
});
