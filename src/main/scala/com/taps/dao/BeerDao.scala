package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.weiglewilczek.slf4s.Logging
import com.taps.model.{Beer, BeerWrapper}
import akka.actor.ActorSystem

/**
 * @author chris carrier
 */

trait BeerDao extends BeerService with Logging {

  val mongoCollection: MongoCollection

  def urlPrefix = "/beers/"

  def search(searchObj: MongoDBObject) = Future {
    val listRes = mongoCollection.find(searchObj).map(f => {
      logger.info(f.toString);
      val pw = grater[BeerWrapper].asObject(f)
      pw.content.copy(id = pw._id)
    }).toList

    val res = listRes match {
      case l: List[Beer] if (!l.isEmpty) => Some(l)
      case _ => None
    }

    res
  }
}

