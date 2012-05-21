package com.taps

import akka.util.Deadline._
import akka.util.Deadline
import java.util.Date
import util._
import net.liftweb.json.{Formats, DefaultFormats}
import com.taps.json.ObjectIdSerializer
import cc.spray._
import cc.spray.http._
import MediaTypes._
import com.mongodb.casbah.Imports._

package object util {
  implicit def deadline2Date(dl: Deadline) = new Date(dl.time.toMillis)
}

package object model {
  implicit def user2Wrapper(user: User): UserWrapper = 
    UserWrapper(user.id, 1, now, now, user)

  implicit def wrapper2User(userWrapper: UserWrapper): User = 
    userWrapper.content.copy(id = userWrapper._id, 
			     dateCreated = Some(userWrapper.dateCreated), 
			     lastUpdated = Some(userWrapper.lastUpdated))

  implicit def beer2Wrapper(beer: Beer): BeerWrapper = 
    BeerWrapper(beer.id, 1, now, now, beer)

  implicit def wrapper2Beer(wrapper: BeerWrapper): Beer = 
    wrapper.content.copy(id = wrapper._id)
 
  implicit def brewery2Wrapper(brewery: Brewery): BreweryWrapper = 
    BreweryWrapper(brewery.id, 1, now, now, brewery)

  implicit def wrapper2Brewery(wrapper: BreweryWrapper): Brewery = 
    wrapper.content.copy(id = wrapper._id)
  
  implicit def place2Wrapper(place: Place): PlaceWrapper = 
    PlaceWrapper(place.id, 1, now, now, place)

  implicit def wrapper2Place(wrapper: PlaceWrapper): Place = 
    wrapper.content.copy(id = wrapper._id)
  
  implicit def beerSearchParams2Dbo(p: BeerSearchParams): MongoDBObject = {
    val query = MongoDBObject()
    p.name.foreach(xs => query += "content.name" -> xs)
    p.description.foreach(xs => query += "content.description" -> xs)
    p.userUrl.foreach(xs => query += "content.userUrl" -> xs)
    query
  }

  implicit def userSearchParams2Dbo(c: UserSearchParams): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    c.email.foreach(builder += "content.email" -> _)
    c.password.foreach(builder += "content.password" -> _)
    builder.result.asDBObject
  }

  implicit def brewerySearchParams2Dbo(p: BrewerySearchParams): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    p.name.foreach(xs => builder += "content.name" -> xs)
    p.description.foreach(xs => builder += "content.description" -> xs)
    builder.result.asDBObject
  }
}

package object endpoint {
  implicit val liftJsonFormats = DefaultFormats + new ObjectIdSerializer

  final val NOT_FOUND_MESSAGE = "resource.notFound"
  final val INTERNAL_ERROR_MESSAGE = "error"

  def JsonContent(content: String) = HttpContent(ContentType(`application/json`), content)

}
