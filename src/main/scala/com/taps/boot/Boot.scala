package com.taps.boot

import cc.spray.io.IoWorker
import cc.spray.{SprayCanRootService, HttpService, RootService}
import util.Properties
import com.taps.mongo.MongoSettings
import com.taps.resource._
import com.taps.dao._
import cc.spray.can.server.HttpServer
import cc.spray.io.pipelines.MessageHandlerDispatch
import akka.actor.{ActorSystem, Props}
import com.weiglewilczek.slf4s.Logging
import com.typesafe.config.ConfigFactory

/** @author chris_carrier
 */

object Boot extends App with Logging {

  logger.info("Running Boot")

  val system = ActorSystem("taps")

  val config = ConfigFactory.load()

  val host = "0.0.0.0"
  val port = config.getInt("spray-can.server.port")

  val mongoUrl = config.getString("mongodb.url")
  val mongoDbName = config.getString("mongodb.database")

  val beerCollection = config.getString("taps.beer.collection")
  val placeCollection = config.getString("taps.place.collection")
  val userCollection = config.getString("taps.user.collection")

  val MongoSettings(db) = Properties.envOrNone("MONGOHQ_URL")

  val beerDao = new BeerDao {
    implicit def actorSystem = system
    val mongoCollection = db(beerCollection)
  }
  val placeDao = new PlaceDao {
    implicit def actorSystem = system
    val mongoCollection = db(placeCollection)
  }
  val userDao = new UserDao {
    implicit def actorSystem = system
    val mongoCollection = db(userCollection)
  }

  // ///////////// INDEXES for collections go here (include all lookup fields)
  //  configsCollection.ensureIndex(MongoDBObject("customerId" -> 1), "idx_customerId")

  val beerModule = new BeerResource {
    implicit def actorSystem = system
    val service = beerDao
  }
  val placeModule = new PlaceResource {
    implicit def actorSystem = system
    val service = placeDao
  }
  val userModule = new UserResource {
    implicit def actorSystem = system
    val service = userDao
  }
  val webAppModule = new WebAppResource {
    implicit def actorSystem = system
  }

  val beerService = system.actorOf(
    props = Props(new HttpService(beerModule.restService)),
    name = "beer-service"
  )
  val placeService = system.actorOf(
    props = Props(new HttpService(placeModule.restService)),
    name = "place-service"
  )
  val userService = system.actorOf(
    props = Props(new HttpService(userModule.restService)),
    name = "user-service"
  )
  val webAppService = system.actorOf(
    props = Props(new HttpService(webAppModule.restService)),
    name = "webApp-service"
  )
  val rootService = system.actorOf(
    props = Props(new SprayCanRootService(beerService, placeService, userService, webAppService)),
    name = "root-service"
  )

  // every spray-can HttpServer (and HttpClient) needs an IoWorker for low-level network IO
  // (but several servers and/or clients can share one)
  val ioWorker = new IoWorker(system).start()

  // create and start the spray-can HttpServer, telling it that we want requests to be
  // handled by the root service actor
  val sprayCanServer = system.actorOf(
    Props(new HttpServer(ioWorker, MessageHandlerDispatch.SingletonHandler(rootService))),
    name = "http-server"
  )

  // a running HttpServer can be bound, unbound and rebound
  // initially to need to tell it where to bind to
  sprayCanServer ! HttpServer.Bind(host, port)

  // finally we drop the main thread but hook the shutdown of
  // our IoWorker into the shutdown of the applications ActorSystem
  system.registerOnTermination {
    ioWorker.stop()
  }
}

