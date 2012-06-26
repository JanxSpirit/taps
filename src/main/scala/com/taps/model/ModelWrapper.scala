package com.taps.model

import java.util.Date

/**
 * @author chris_carrier
 * @version 5/19/12
 */


case class BeerWrapper(_id: Option[String],
                       version: Long,
                       dateCreated: Date,
                       lastUpdated: Date,
                       content: Beer)

case class UserWrapper(_id: Option[String],
                       version: Long,
                       dateCreated: Date,
                       lastUpdated: Date,
                       content: User)

case class BreweryWrapper(_id: Option[String],
                       version: Long,
                       dateCreated: Date,
                       lastUpdated: Date,
                       content: Brewery)

case class PlaceWrapper(_id: Option[String],
                       version: Long,
                       dateCreated: Date,
                       lastUpdated: Date,
                       content: Place)

case class PlaceBeerWrapper(_id: Option[String],
			    version: Long,
			    dateCreated: Date,
			    lastUpdated: Date,
			    content: PlaceBeer)
