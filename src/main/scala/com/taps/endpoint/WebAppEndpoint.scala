package com.taps.endpoint

import cc.spray.Directives
import com.weiglewilczek.slf4s.Logging

/**
 * @author chris_carrier
 * @version 1/14/12
 */


trait WebAppEndpoint extends Directives with Logging {

  logger.info("Starting web ap endpoint.")

  val appPath =  path("")

  val restService = {
    appPath {
      cache {
        getFromResource("index.html")
      }
    } ~
      pathPrefix("css") {
        cache {
          getFromResourceDirectory("assets/css")
        }
      } ~
      pathPrefix("app") {
        cache {
          getFromResourceDirectory("app")
        }
      } ~
      pathPrefix("js") {
        cache {
          getFromResourceDirectory("assets/js")
        }
      } ~
      pathPrefix("img") {
        cache {
          getFromResourceDirectory("assets/img")
        }
      } ~
      pathPrefix("templates") {
        cache {
          getFromResourceDirectory("templates")
        }
      } ~
      path("webAppPing") {
        get {
          _.complete("Yo!")
        }
      }
  }

}