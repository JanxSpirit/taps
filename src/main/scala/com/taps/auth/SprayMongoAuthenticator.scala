package com.taps.auth

import org.bson.types.ObjectId
import cc.spray._
import cc.spray.http.{BasicHttpCredentials, HttpCredentials}
import com.mongodb.casbah.commons.Imports._
import com.novus.salat._
import com.novus.salat.global._
import scala.util.Properties
import com.taps.mongo.MongoSettings
import com.taps.model.{UserWrapper, User}
import com.mongodb.casbah.commons.{MongoDBObject}
import com.weiglewilczek.slf4s.Logging
import akka.actor.ActorSystem
import akka.dispatch.{ExecutionContext, Future}

/**
 * @author chris_carrier
 * @version 10/19/11
 */

object FromMongoUserPassAuthenticator extends Logging {
  def apply()(implicit executor: ExecutionContext): UserPassAuthenticator[User] = {
    new UserPassAuthenticator[User] {
      def apply(userPass: Option[(String, String)]) = {
        logger.info("Mongo auth")
        Future {
          userPass.flatMap {
            case (user, pass) => {
              logger.info("Autenticating: " + user + " " + pass)
              val MongoSettings(db) = Properties.envOrNone("MONGOHQ_URL")
              val userColl = db("users")
              val userResult = userColl.findOne(MongoDBObject("content.email" -> user) ++ ("content.password" -> pass))
              userResult.map(grater[UserWrapper].asObject(_))
            }
            case _ => None
          }
        }
      }
    }
  }
  
}