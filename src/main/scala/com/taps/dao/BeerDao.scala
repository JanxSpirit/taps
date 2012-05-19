package com.taps.dao

import com.mongodb.casbah.Imports._
import akka.dispatch.Future
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.commons.MongoDBObject
import com.mycotrack.api._
import model._
import mongo.RandomId
import cc.spray.utils.Logging

/**
 * @author chris carrier
 */

trait BeerDao extends IProjectDao with Logging {

  val mongoCollection: MongoCollection

  def urlPrefix = "/projects/"

  def search(searchObj: MongoDBObject) = Future {
    val listRes = mongoCollection.find(searchObj).map(f => {
      log.info(f.toString);
      val pw = grater[ProjectWrapper].asObject(f)
      pw.content.head.copy(id = pw._id)
    }).toList

    val res = listRes match {
      case l: List[Project] if (!l.isEmpty) => Some(l)
      case _ => None
    }

    res
  }

  def getChildren(root: Project) = Future {
    val query = MongoDBObject("parent" -> root.id)
    mongoCollection.find(query).map(f =>
      grater[ProjectWrapper].asObject(f).content).toList match {
      case l: List[Project] if (!l.isEmpty) => Some(l)
      case _ => None
    }
  }
}

