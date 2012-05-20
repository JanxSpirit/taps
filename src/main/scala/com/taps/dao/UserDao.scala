package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.commons.MongoDBObject
import com.taps.model.{UserWrapper, User}

/**
 * @author chris carrier
 */

trait UserDao extends UserService {

  val mongoCollection: MongoCollection

  def urlPrefix = "/users/"

  def search(searchObj: MongoDBObject) = {
    Future {
      val listRes: List[User] = mongoCollection.find(searchObj).map(f => {
        val u: User = grater[UserWrapper].asObject(f)
        u
      }).toList

      val res = listRes match {
        case l: List[User] if (!l.isEmpty) => Some(l)
        case _ => None
      }

      res
    }
  }
}

