package com.taps.resource

import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import net.liftweb.json.DefaultFormats

trait MycotrackSpec {
  implicit val formats = DefaultFormats

  val BASE_URL = "/%s" format resourceName
  val config = ConfigFactory.load("test")

  lazy val mongoUrl = config.getString("mongodb.url")
  lazy val mongoDbName = config.getString("mongodb.database")
  lazy val collection = config.getString("mycotrack.%s.collection") format resourceName
  lazy val port = config.getInt("mongodb.port")
  lazy val db = MongoConnection(mongoUrl, 27017)(mongoDbName)
  lazy val configDb = db(collection)

  def resourceName: String
}
