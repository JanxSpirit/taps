package com.taps.endpoint

import org.bson.types.ObjectId
import cc.spray.http._
import cc.spray.typeconversion._
import HttpHeaders._
import HttpMethods._
import StatusCodes._
import MediaTypes._
import cc.spray.authentication._
import net.liftweb.json.JsonParser._
import net.liftweb.json.Serialization._
import net.liftweb.json.{Formats, DefaultFormats}
import cc.spray._
import akka.dispatch.Future
import caching._
import caching.LruCache._
import com.weiglewilczek.slf4s.Logging
import com.taps.dao.BeerService
import com.taps.response.ErrorResponse
import com.taps.auth.FromMongoUserPassAuthenticator
import com.taps.model.{BeerSearchParams, Beer, BeerWrapper}
import com.taps.json.ObjectIdSerializer

/**
 * @author chris carrier
 */

trait BeerEndpoint extends Directives with LiftJsonSupport with Logging {
  implicit val liftJsonFormats = DefaultFormats + new ObjectIdSerializer

  final val NOT_FOUND_MESSAGE = "resource.notFound"
  final val INTERNAL_ERROR_MESSAGE = "error"

  def JsonContent(content: String) = HttpContent(ContentType(`application/json`), content)

  val requiredFields = List("name", "description")

  //caches
  lazy val beerCache: Cache[Either[Set[Rejection], HttpResponse]] = LruCache(100)

  val service: BeerService

  def withSuccessCallback(ctx: RequestContext, statusCode: StatusCode = OK)(f: Future[_]): Future[_] = {
    f.onComplete(f => {
      f match {
        case Right(Some(BeerWrapper(oid, version, dateCreated, lastUpdated, content))) => ctx.complete(statusCode, content.copy(id = oid))
        case Right(Some(c: Beer)) => ctx.complete(c)
        case _ => ctx.fail(StatusCodes.NotFound, ErrorResponse(1l, ctx.request.path, List(NOT_FOUND_MESSAGE)))
      }
    })
  }

  //directive compositions
  val alphanumericMatch = path("^[a-zA-Z0-9]+$".r)
  //val directGetBeer = authenticate(httpMongo(realm = "taps")) & get
  val directGetBeer = get
  val putBeer = content(as[Beer]) & put
  val postBeer = path("") & content(as[Beer]) & post
  val indirectGetBeers = path("") & parameters('name ?, 'description ?) & get

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
      pathPrefix("beers") {
        authenticate(httpMongo(realm = "taps")) { user =>
          alphanumericMatch {
          resourceId =>
              cacheResults(beerCache) {
                directGetBeer {
                  ctx =>
                        withSuccessCallback(ctx) {
                          service.get[BeerWrapper](service.formatKeyAsId(resourceId), user.id)
                        }

                    }
            } ~
              putBeer {
                resource => ctx =>
                      withSuccessCallback(ctx) {
                        service.update[Beer, BeerWrapper](resourceId, resource)
                      }
              }
        } ~
          postBeer {
            resource => ctx =>
                withSuccessCallback(ctx, Created) {
                  service.create[BeerWrapper](resource.copy(userUrl = user.id))
                }



          } ~
          indirectGetBeers {
            (name, description) => ctx =>
                service.search(BeerSearchParams(name, description, user.id)).onComplete(f => {
                  logger.info("User: " + user.toString)
                  f match {
                    case Right(Some(content)) => {
                      val res: List[Beer] = content
                      ctx.complete(res)
                    }
                    case _ => ctx.fail(StatusCodes.NotFound, ErrorResponse(1, ctx.request.path, List(NOT_FOUND_MESSAGE)))
                  }
                })

          }
        }

      }


  }

  def httpMongo[U](realm: String = "Secured Resource",
                   authenticator: UserPassAuthenticator[U] = FromMongoUserPassAuthenticator.apply())
  : BasicHttpAuthenticator[U] =
    new BasicHttpAuthenticator[U](realm, authenticator)


}