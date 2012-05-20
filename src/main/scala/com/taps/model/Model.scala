package com.taps.model

import org.bson.types.ObjectId
import com.novus.salat.annotations.Ignore
import java.util.Date

case class Beer(@Ignore id: Option[String],
                   description: Option[String],
                   name: Option[String],
                   userUrl: Option[String])

object Beer {
  implicit def beer2Wrapper(beer: Beer): BeerWrapper = {
    val now = new Date
    BeerWrapper(beer.id, 1, now, now, beer)
  }
}


case class User(@Ignore id: Option[String], @Ignore dateCreated: Option[Date], @Ignore lastUpdated: Option[Date], email: String, password: String)
object User {
  implicit def user2UserWrapper(user: User): UserWrapper = {
    val now = new Date
    UserWrapper(user.id, 1, now, now, user)
  }
}