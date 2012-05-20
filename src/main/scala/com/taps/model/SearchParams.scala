package com.taps.model

import com.mongodb.casbah.Imports._

case class BeerSearchParams(name: Option[String], description: Option[String], userUrl: Option[String])
object BeerSearchParams {
  implicit def toDbo(p: BeerSearchParams): MongoDBObject = {
    val query = MongoDBObject()
    p.name.foreach(xs => query += "content.name" -> xs)
    p.description.foreach(xs => query += "content.description" -> xs)
    p.userUrl.foreach(xs => query += "content.userUrl" -> xs)
    query
  }
}

case class UserSearchParams(email: Option[String], password: Option[String])
object UserSearchParams {
  implicit def toDbo(c: UserSearchParams): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    c.email.foreach(builder += "content.email" -> _)
    c.password.foreach(builder += "content.password" -> _)
    builder.result.asDBObject
  }
}