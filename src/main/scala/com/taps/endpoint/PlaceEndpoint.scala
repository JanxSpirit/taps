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
import com.taps.dao.PlaceService
import com.taps.model.PlaceBeer
import com.taps.model.{ Place, PlaceSearchParams, PlaceWrapper }
import com.taps.json.ObjectIdSerializer
import com.taps.response.ErrorResponse
import com.weiglewilczek.slf4s.Logging
import net.liftweb.json.JsonParser._
import net.liftweb.json.Serialization._
import net.liftweb.json.{ Formats, DefaultFormats }

trait PlaceEndpoint extends Directives with LiftJsonSupport with Logging {
  implicit val liftJsonFormats = DefaultFormats + new ObjectIdSerializer
  val service: PlaceService

  //caches
  lazy val placeCache: Cache[Either[Set[Rejection], HttpResponse]] = LruCache(100)

  //directive compositions
  val alphanumericMatch = path("^[a-zA-Z0-9]+$".r)
  val directGetPlace = get
  val putPlace = content(as[Place]) & put
  val postPlace = path("") & content(as[Place]) & post
  val indirectGetPlaces = path("") & parameters('name ?,
    'radius ?,
    'nearLat ?,
    'nearLon ?) & get
  val postPlaceBeer = alphanumericMatch & path("beers") & content(as[PlaceBeer]) & post

  def withSuccessCallback(ctx: RequestContext, statusCode: StatusCode = OK)(f: Future[_]): Future[_] = {
    f.onComplete(f => {
      f match {
        case Right(Some(PlaceWrapper(oid, version, dateCreated, lastUpdated, content))) =>
          ctx.complete(statusCode, content.copy(id = oid))
        case Right(Some(c: Place)) => ctx.complete(c)
        case _ => ctx.fail(StatusCodes.NotFound,
          ErrorResponse(1l, ctx.request.path, List(NOT_FOUND_MESSAGE)))
      }
    })
  }

  def httpMongo[U](realm: String = "Secured Resource",
    authenticator: UserPassAuthenticator[U] = FromMongoUserPassAuthenticator.apply()): BasicHttpAuthenticator[U] =
    new BasicHttpAuthenticator[U](realm, authenticator)

  val restService = {
    // Service implementation.
    pathPrefix("places") {
      authenticate(httpMongo(realm = "taps")) { user =>
        alphanumericMatch { resourceId =>
          cacheResults(placeCache) {
            respondWithHeader(CustomHeader("TEST", "Awesome")) {
              directGetPlace {
                ctx =>
                  withSuccessCallback(ctx) {
                    service.get[PlaceWrapper](service.formatKeyAsId(resourceId), user.id)
                  }
              }
            }
          } ~
            putPlace { resource =>
              ctx =>
                withSuccessCallback(ctx) {
                  service.update[Place, PlaceWrapper](resourceId, resource)
                }
            } ~
            postPlaceBeer { (placeId, placeBeer) =>
              ctx =>
                ctx.complete("palceId = %s; placeBeer = %s".format(placeId, placeBeer))
            }
        } ~
          postPlace { resource =>
            ctx =>
              withSuccessCallback(ctx, Created) {
                service.create[PlaceWrapper](resource)
              }
          } ~
          indirectGetPlaces {
            (name, radius, nearLat, nearLon) =>
              ctx =>
                service.search(PlaceSearchParams(name)).onComplete(f => {
                  logger.info("User: " + user.toString)
                  f match {
                    case Right(Some(content)) => {
                      val res: List[Place] = content
                      ctx.complete(res)
                    }
                    case _ => ctx.fail(StatusCodes.NotFound,
                      ErrorResponse(1, ctx.request.path, List(NOT_FOUND_MESSAGE)))
                  }
                })
          }
      }
    }
  }
}
