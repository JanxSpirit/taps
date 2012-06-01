package com.taps.model

import org.bson.types.ObjectId
import com.novus.salat.annotations.Ignore
import java.util.Date
import akka.util.Deadline._

case class Beer(@Ignore id: Option[String],
                    description: Option[String],
                    name: Option[String],
                    userUrl: Option[String],
		    breweryId: String)

case class User(@Ignore id: Option[String], 
		    @Ignore dateCreated: Option[Date], 
		    @Ignore lastUpdated: Option[Date], 
		    email: String, 
		    password: String,
		    favBeers: List[String] = Nil,
		    unfavBeers: List[String] = Nil,
		    favBreweries: List[String] = Nil,
		    unfavBreweries: List[String] = Nil)

case class Brewery(@Ignore id: Option[String], 
		    description: Option[String],
		    name: String,
		    location: Option[Location],
		    website: Option[String],
		    beers: Seq[String])

case class Place(@Ignore id: Option[String], 
		    description: Option[String],
		    name: String,
		    location: Option[Location],
		    website: Option[String],
		    bottle: Option[Seq[String]],
		    draft: Option[Seq[String]])

case class Location(city: Option[String],
		    state: Option[String],
		    country: Option[String],
		    streetAddress: Option[String],
		    zip: Option[Int],
		    lat: Option[Double],
		    lon: Option[Double],
		    latlon: Seq[Double])
