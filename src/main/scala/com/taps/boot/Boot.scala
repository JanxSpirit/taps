package com.taps.boot

import cc.spray.io.IoWorker
import cc.spray.{SprayCanRootService, HttpService, RootService}
import util.Properties
import com.taps.mongo.MongoSettings
import com.taps.endpoint.{WebAppEndpoint, UserEndpoint, BeerEndpoint}
import com.taps.dao.{UserDao, BeerDao}
import cc.spray.can.server.HttpServer
import cc.spray.io.pipelines.MessageHandlerDispatch
import akka.actor.{ActorSystem, Props}
import com.weiglewilczek.slf4s.Logging
import com.typesafe.config.ConfigFactory

/**
 * @author chris_carrier
 */

object Boot extends App with Logging {

  logger.info("Running Boot")

  val system = ActorSystem("demo")

  val akkaConfig = ConfigFactory.load()

  val host = "0.0.0.0"
  val port = Option(System.getenv("PORT")).getOrElse("8080").toInt

  val mongoUrl = akkaConfig.getString("mongodb.url")
  val mongoDbName = akkaConfig.getString("mongodb.database")

  val beerCollection = akkaConfig.getString("taps.beer.collection")
  val userCollection = akkaConfig.getString("taps.user.collection")

//  val urlList = mongoUrl.split(",").toList.map(new ServerAddress(_))

  val MongoSettings(db) = Properties.envOrNone("MONGOHQ_URL")

  val beerDao = new BeerDao {
    implicit def actorSystem = system
    val mongoCollection = db(beerCollection)
  }
  val userDao = new UserDao {
    implicit def actorSystem = system
    val mongoCollection = db(userCollection)
  }

  // ///////////// INDEXES for collections go here (include all lookup fields)
  //  configsCollection.ensureIndex(MongoDBObject("customerId" -> 1), "idx_customerId")
  val beerModule = new BeerEndpoint {
    implicit def actorSystem = system
    val service = beerDao
  }
  val userModule = new UserEndpoint {
    implicit def actorSystem = system
    val service = userDao
  }
  val webAppModule = new WebAppEndpoint {
    implicit def actorSystem = system
  }

  val beerService = system.actorOf(
    props = Props(new HttpService(beerModule.restService)),
    name = "beer-service"
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
    props = Props(new SprayCanRootService(beerService, userService, webAppService)),
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

