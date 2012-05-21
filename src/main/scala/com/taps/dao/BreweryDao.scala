package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.commons.MongoDBObject
import com.taps.model.{BreweryWrapper, Brewery}

trait BreweryDao extends BreweryService {

  val mongoCollection: MongoCollection

  def urlPrefix = "/breweries/"

  def search(searchObj: MongoDBObject) = {
    Future {
      val listRes: List[Brewery] = mongoCollection.find(searchObj).map(f => {
        val u: Brewery = grater[BreweryWrapper].asObject(f)
        u
      }).toList

      val res = listRes match {
        case l: List[Brewery] if (!l.isEmpty) => Some(l)
        case _ => None
      }

      res
    }
  }
}
