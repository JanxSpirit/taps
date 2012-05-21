package com.taps.model

import org.bson.types.ObjectId
import com.novus.salat.annotations.Ignore
import java.util.Date
import akka.util.Deadline._

case class Beer(@Ignore id: Option[String],
                   description: Option[String],
                   name: Option[String],
                   userUrl: Option[String])

case class User(@Ignore id: Option[String], 
		@Ignore dateCreated: Option[Date], 
		@Ignore lastUpdated: Option[Date], 
		email: String, password: String)


