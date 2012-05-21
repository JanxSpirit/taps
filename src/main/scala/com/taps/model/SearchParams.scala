package com.taps.model

import com.mongodb.casbah.Imports._

case class BeerSearchParams(name: Option[String], 
			    description: Option[String], 
			    userUrl: Option[String])

case class UserSearchParams(email: Option[String], 
			    password: Option[String])

case class BrewerySearchParams(name: Option[String],
			       description: Option[String])

case class PlaceSearchParams(name: Option[String])
