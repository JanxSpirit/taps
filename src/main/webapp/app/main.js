require([
  "namespace",

  // Libs
  "jquery",
  "use!underscore",
  "use!backbone",
  "modelbinding",
  "use!base64",

  // Modules
  "modules/taps-layout",
  "modules/views/navbar",
  "modules/models/beer",
  "modules/views/base",
  "modules/models/user",
  "modules/views/login",

  //plugins
  "use!bootstrapdatepicker",
  "use!gx"
],

function(namespace, jQuery, _, Backbone, ModelBinding, Base64, Taps, Navbar, Beer, BaseView, User, Login) {

  // Defining the application router, you can attach sub routers here.
  var Router = Backbone.Router.extend({
    initialize: function() {
        namespace.app.user = new User.Model();

        namespace.app.loginForm = new Navbar.Views.LoginForm({
            model: namespace.app.user
        });

        namespace.app.navBarView = new Navbar.Views.Navbar({
            views: {
                "#loginanchor": namespace.app.loginForm
            }
        });

        namespace.app.homeView = new BaseView.Home({
                    views: {
                    }
                });

        namespace.app.newUserView = new BaseView.NewUser();

        namespace.app.main = new Backbone.LayoutManager({
                     template: "base",
                     views: {
                         "#tapsnav": namespace.app.navBarView
                     }
                   });
        namespace.app.main.render(function(el) {
            $("#main").html(el);
            ModelBinding.bind(namespace.app.loginForm);
        });

        //global event listeners
        namespace.app.on('auth:required', function(eventName){
            console.log('Login necessary!');
          });

          namespace.app.on('login:submit', function(eventName){
            namespace.app.user.fetch({
                success: function(){
                    $("#loginanchor").detach();
                },
                error: function(){
                    var loginModal = new Login.View({
                        model: namespace.app.user
                    });
                    loginModal.render();
                    ModelBinding.bind(loginModal);
                }
            });
        });
    },

    routes: {
      "": "index",
      "new_user": "newUser",
      "login": "login"
    },

    index: function() {
        var route = this;

        var beer = new Beer.Collection();
        beer.fetch();

        namespace.app.main.view("#contentAnchor", namespace.app.homeView);
        namespace.app.homeView.render();
    },

    newUser: function(hash) {
          var route = this;
          var newUser = new User.Model({});

          namespace.app.newUserView.model = newUser;
          namespace.app.main.view("#contentAnchor", namespace.app.newUserView);
          namespace.app.newUserView.render();

        }
  });

  // Shorthand the application namespace
  var app = namespace.app;

  // Treat the jQuery ready function as the entry point to the application.
  // Inside this function, kick-off all initialization, everything up to this
  // point should be definitions.
  jQuery(function($) {
    // Define your master router on the application namespace and trigger all
    // navigation from this instance.
    app.router = new Router();

    // Trigger the initial route and enable HTML5 History API support
    Backbone.history.start({ pushState: true });

    $.ajaxSetup({
        beforeSend: function (xhr) {
            if (namespace.app.user.get('email')) {
                console.log("Before sending!");
                var authString = namespace.app.user.get('email') + ":" + namespace.app.user.get('password');
                 var encodedAuthString = "Basic " + Base64.encode(authString);

                 console.log("Authorizing with: " + authString + "with encoded: " + encodedAuthString);

                xhr.setRequestHeader('Authorization', encodedAuthString);
            }
            return xhr;
	    },
        statusCode: {
            401: function(){
                console.log("HANDLED 401");
                namespace.app.trigger('auth:required');

            },
            403: function(){
                console.log("HANDLED 403");
                namespace.app.trigger('auth:required');

            }
        }
      });
  });

  // All navigation that is relative should be passed through the navigate
  // method, to be processed by the router.  If the link has a data-bypass
  // attribute, bypass the delegation completely.
  $(document).on("click", "a:not([data-bypass])", function(evt) {
    // Get the anchor href and protcol
    var href = $(this).attr("href");
    var protocol = this.protocol + "//";

    // Ensure the protocol is not part of URL, meaning its relative.
    if (href && href.slice(0, protocol.length) !== protocol &&
        href.indexOf("javascript:") !== 0) {
      // Stop the default event to ensure the link will not cause a page
      // refresh.
      evt.preventDefault();

      // This uses the default router defined above, and not any routers
      // that may be placed in modules.  To have this work globally (at the
      // cost of losing all route events) you can change the following line
      // to: Backbone.history.navigate(href, true);
      app.router.navigate(href, true);
    }
  });

});
