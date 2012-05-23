define([
  "namespace",

  // Libs
  "jquery",
  "use!underscore",
  "use!backbone",
  "modelbinding"

  // Modules

  // Plugins
],

function(namespace, $, _, Backbone, ModelBinding) {

  // Create a new module
  var BaseView = namespace.module();

  BaseView.Home = Backbone.View.extend({
    template: "base/home_base"
  });

  BaseView.NewUser = Backbone.View.extend({
      template: "base/new_user",

      events: {
          "click #user-submit": "saveSelected"
      },

      saveSelected: function() {
          var view = this;
          ModelBinding.bind(this);
          console.log('Saving new: ' + JSON.stringify(this.model));
          this.model.save({}, {success: function(model, response){
              namespace.app.router.navigate("/bb_mt", true);
          }});

      },

      serialize: function() {
        return this.model.toJSON();
      }
    });

  // Required, return the module for AMD compliance
  return BaseView;

});
