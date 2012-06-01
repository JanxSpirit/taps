package com.taps.endpoint

import akka.dispatch.Future
import cc.spray._
import cc.spray.authentication._
import cc.spray.caching._
import cc.spray.caching.LruCache._
import cc.spray.http._
import cc.spray.http.HttpHeaders._
import cc.spray.http.HttpMethods._
import cc.spray.http.MediaTypes._
import cc.spray.http.StatusCodes._
import cc.spray.typeconversion._
import com.taps.auth.FromMongoUserPassAuthenticator
import com.taps.dao.BreweryService
import com.taps.model.{Brewery, BrewerySearchParams, BreweryWrapper}
import com.taps.response.ErrorResponse
import com.weiglewilczek.slf4s.Logging
import net.liftweb.json.JsonParser._
import net.liftweb.json.Serialization._

trait BreweryEndpoint extends Directives with LiftJsonSupport with Logging {

  //caches
  lazy val breweryCache: Cache[Either[Set[Rejection], HttpResponse]] = LruCache(100)

  val service: BreweryService

  //directive compositions
  val alphanumericMatch = path("^[a-zA-Z0-9]+$".r)

  def withSuccessCallback(ctx: RequestContext, statusCode: StatusCode = OK)(f: Future[_]): Future[_] = {
    f.onComplete(f => {
      f match {
        case Right(Some(BreweryWrapper(oid, version, dateCreated, lastUpdated, content))) => ctx.complete(statusCode, content.copy(id = oid))
        case Right(Some(c: Brewery)) => ctx.complete(c)
        case _ => ctx.fail(StatusCodes.NotFound, 
			   ErrorResponse(1l, ctx.request.path, List(NOT_FOUND_MESSAGE)))
      }
    })
  }

  val directGetBrewery = get
  val putBrewery = content(as[Brewery]) & put
  val postBrewery = path("") & content(as[Brewery]) & post
  val indirectGetBreweries = path("") & parameters('name ?, 'description ?) & get

  val restService = {
    // Debugging: /ping -> pong
    path("ping") {
      detach{
      cache {
        get {
          _.complete("pong " + new java.util.Date())
        }
      }
      }
    } ~
      // Service implementation.
      pathPrefix("breweries") {
        authenticate(httpMongo(realm = "taps")) { user =>
          alphanumericMatch {
          resourceId =>
              cacheResults(breweryCache) {
                respondWithHeader(CustomHeader("TEST", "Awesome")){
                directGetBrewery {
                  ctx =>
                        withSuccessCallback(ctx) {
                          service.get[BreweryWrapper](service.formatKeyAsId(resourceId), user.id)
                        }
                    }
                }
            } ~
              putBrewery {
                resource => ctx =>
                      withSuccessCallback(ctx) {
                        service.update[Brewery, BreweryWrapper](resourceId, resource)
                      }
              }
        } ~
          postBrewery {
            resource => ctx =>
                withSuccessCallback(ctx, Created) {
                  service.create[BreweryWrapper](resource)
                }
          } ~
          indirectGetBreweries {
            (name, description) => ctx =>
                service.search(BrewerySearchParams(name, description)).onComplete(f => {
                  logger.info("User: " + user.toString)
                  f match {
                    case Right(Some(content)) => {
                      val res: List[Brewery] = content
                      ctx.complete(res)
                    }
                    case _ => ctx.fail(StatusCodes.NotFound, ErrorResponse(1, ctx.request.path, List(NOT_FOUND_MESSAGE)))
                  }
                })
          /*} ~
	  pathPrefix("favorites") {
	    (userName) => ctx =>
	      service.getFavoritesForUser(userName).onComplete(favs => {
		
	      })*/
	  }
        } 
      }
  }

  def httpMongo[U](realm: String = "Secured Resource",
                   authenticator: UserPassAuthenticator[U] = FromMongoUserPassAuthenticator.apply())
  : BasicHttpAuthenticator[U] =
    new BasicHttpAuthenticator[U](realm, authenticator)


}
