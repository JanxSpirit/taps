package com.taps.dao
import akka.actor.ActorSystem

import akka.dispatch.Future
import com.taps.model.{PlaceBeer, PlaceBeerWrapper}
import com.weiglewilczek.slf4s.Logging
import com.mongodb.casbah.Imports._
import com.taps.model._
import com.novus.salat._
import com.novus.salat.global._

trait PlaceBeerDao extends PlaceBeerService with Logging {
  
  implicit def actorSystem: ActorSystem

  val mongoCollection: MongoCollection

  def urlPrefix = "//"

  //assume placeId/beerId/format is a unique id
  def createOrUpdate(model: PlaceBeer) = {
    Future {
      //what to do about none format exists 
      //and then a real format is added? anything? handle it client side?
      //only return none format if no other formats exist?

      //basic plan here is to:
      //1 - get the wrapper
      //?no - upsert a new wrapper
      //?yes - update wrapper with new content
      //this is a race condition but is not a concern because multiple
      //upserts will only result in a slightly inaccurate set of wrapper
      //metadata, but not enough to matter.
      val id = buildPlaceBeerId(model.placeId, 
		       model.beerId, 
		       model.format.getOrElse(NONE_FORMAT))
      mongoCollection.findOne(MongoDBObject("_id" -> id)) match {
        case Some(w) => {
	  mongoCollection.update(
	    MongoDBObject("_id" -> id),
	    $set("content" -> grater[PlaceBeer].asDBObject(model)),
	    false,
	    false)
	}
	case _ => {
	  mongoCollection.update(
	    MongoDBObject("_id" -> id),
	    grater[PlaceBeerWrapper].asDBObject(model),
	    true,
	    false)
	}
      }
      Some(grater[PlaceBeerWrapper].asObject(
	mongoCollection.findOne(MongoDBObject("_id" -> id)).getOrElse(MongoDBObject())))
    }
  }

}
