define([
  "namespace",

  // Libs
  "jquery",
  "use!underscore",
  "use!backbone"

  // Modules

  // Plugins
],

function(namespace, $, _, Backbone) {

  // Create a new module
  var Beer = namespace.module();

  // Example extendings
  Beer.Model = Backbone.Model.extend({
        urlRoot: '/beer',

        url: function() {
            if (!this.isNew()){
                return this.id;
            }
          return this.urlRoot;
        },

        parse: function(response) {
            var content = response;
            return content;
        }
    });

  Beer.Collection = Backbone.Collection.extend({
        url: '/beer',

        model: Beer.Model
   });

  // Required, return the module for AMD compliance
  return Beer;

});
