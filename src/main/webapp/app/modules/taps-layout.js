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
  var Taps = namespace.module();

  Taps.Views.BeerView = Backbone.View.extend({
    template: "beer_list_hb",

    tagName: "li",

    serialize: function() {
      return this.model.toJSON();
    }
  });

  // This will fetch the tutorial template and render it.
  Taps.Views.BeerList = Backbone.View.extend({
    tagName: "ul",
    className: 'beers nav nav-tabs nav-stacked',

    initialize: function(){
        console.log('Initializing projectListView');
        _.bindAll('render');

    },

    render: function(manage) {
        // Have LayoutManager manage this View and call render.
        var view = manage(this);
        var ctx = this.options.context;

//        if (!namespace.app.initialized) {
//            namespace.app.initialized = true;
            // Iterate over the passed collection and create a view for each item
            console.log(this.collection);
            this.collection.each(function(model) {
              // Pass the data to the new SomeItem view
              console.log('Adding project');
              var projectView = new Taps.Views.ProjectView({
                model: model,
                context: ctx
              });
              view.insert(projectView);
            });
//        }

        // You still must return this view to render, works identical to
        // existing functionality.
        return view.render();
      }

  });

  // Required, return the module for AMD compliance
  return Taps;

});
