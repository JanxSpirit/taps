package com.taps.resource

import akka.actor.ActorSystem
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import net.liftweb.json.DefaultFormats
import net.liftweb.json.NoTypeHints
import net.liftweb.json.Serialization

trait TapsSpec {
  lazy val system = ActorSystem("taps_test")

  val BASE_URL = "/%s" format resourceName
  val config = ConfigFactory.load("test")

  lazy val mongoUrl = config.getString("mongodb.url")
  lazy val mongoDbName = config.getString("mongodb.database")
  lazy val collection = config.getString("taps.%s.collection") format resourceName
  lazy val port = config.getInt("mongodb.port")
  lazy val db = MongoConnection(mongoUrl, port)(mongoDbName)
  lazy val configDb = db(collection)

  def resourceName: String
}
