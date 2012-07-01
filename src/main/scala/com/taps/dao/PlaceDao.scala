package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.commons.MongoDBObject
import com.taps.model.{PlaceWrapper, Place}

trait PlaceDao extends PlaceService {

  def mongoCollection: MongoCollection

  def urlPrefix = "/places/"

  def search(searchObj: MongoDBObject) = {
    Future {
      val listRes: List[Place] = mongoCollection.find(searchObj).map(f => {
        val u: Place = grater[PlaceWrapper].asObject(f)
        u
      }).toList

      val res = listRes match {
        case l: List[Place] if (!l.isEmpty) => Some(l)
        case _ => None
      }

      res
    }
  }
}
