package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.taps.model.PlaceBeer
import com.taps.model.PlaceBeerWrapper
import com.taps.model.PlaceWrapper
import scala.reflect.Manifest
import com.taps.mongo.RandomId
import akka.event.Logging
import com.taps.model.{UserWrapper, User, 
		       BeerWrapper, Beer, 
		       Brewery, BreweryWrapper, 
		       Place, PlaceWrapper}
import akka.actor.ActorSystem

trait TapsDao[T <: CaseClass, W <: CaseClass] {
  implicit def actorSystem: ActorSystem
  def mongoCollection: MongoCollection

  def urlPrefix: String
  def formatKeyAsId(s: String): String = {
    urlPrefix + s
  }
  def nextRandomId: String = {
    formatKeyAsId(RandomId.getNextValue.get)
  }

  def getByKey(key: String): Future[Option[T]] = {
    getByKey(key, None)
  }
  def getByKey(key: String, userId: Option[String]): Future[Option[T]] = {
    get(formatKeyAsId(key), userId)
  }
  def get[TT <: W](id: String, userId: Option[String] = None)(implicit man: Manifest[TT]): Future[Option[TT]] = {
    Future {
      val builder = MongoDBObject.newBuilder
      builder += ("_id" -> id)
      userId.foreach(builder += "content.userUrl" -> _)

      val dbo = mongoCollection.findOne(builder.result.asDBObject)
      val result = dbo.map(f => grater[TT].asObject(f))

      result
    }
  }

  def create[TT <: W](wrapper: TT)(implicit man: Manifest[TT]): Future[Option[TT]] = {
    Future {
      val dbo = grater[TT].asDBObject(wrapper)
      val builder = MongoDBObject.newBuilder
      builder ++= dbo.toList
      builder += ("_id" -> Some(nextRandomId))
      val toSave = builder.result
      mongoCollection += toSave
      Some(grater[TT].asObject(toSave))
    }
  }

  def update[TT <: T, WW <: W](key: String, model: TT)(implicit man: Manifest[TT], manW: Manifest[WW]): Future[Option[WW]] = {
    Future {
      val inputDbo = grater[TT].asDBObject(model)
      val query = MongoDBObject("_id" -> formatKeyAsId(key))
      val update = $set("content" -> List(inputDbo))

      mongoCollection.update(query, update, false, false, WriteConcern.Safe)

      val dbo = mongoCollection.findOne(query)
      val result = dbo.map(f => grater[WW].asObject(f))

      result
    }
  }
}

trait BeerService extends TapsDao[Beer, BeerWrapper] {
  def search(searchObj: MongoDBObject): Future[Option[List[Beer]]]
}

trait UserService extends TapsDao[User, UserWrapper] {
}

trait BreweryService extends TapsDao[Brewery, BreweryWrapper] {
  def search(searchObj: MongoDBObject): Future[Option[List[Brewery]]]
}

trait PlaceService extends TapsDao[Place, PlaceWrapper] {
  def search(searchObj: MongoDBObject): Future[Option[List[Place]]]
}

trait PlaceBeerService extends TapsDao[PlaceBeer, PlaceBeerWrapper]
