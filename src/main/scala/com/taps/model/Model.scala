package com.taps.model

import org.bson.types.ObjectId
import com.novus.salat.annotations.Ignore
import java.util.Date

case class Project(@Ignore id: Option[String],
                   description: Option[String],
                   cultureUrl: Option[String],
                   userUrl: Option[String],
                   enabled: Boolean,
                   substrate: Option[String],
                   container: Option[String],
                   startDate: Option[Date],
                   parent: Option[ObjectId] = None,
                   @Ignore timestamp: Option[Date] = Some(new Date()),
                    count: Option[Long])

case class Project(@Ignore id: Option[String],
                   description: Option[String],
                   cultureUrl: Option[String],
                   userUrl: Option[String],
                   enabled: Boolean,
                   substrate: Option[String],
                   container: Option[String],
                   startDate: Option[Date],
                   parent: Option[ObjectId] = None,
                   @Ignore timestamp: Option[Date] = Some(new Date()),
                    count: Option[Long])

case class Species(@Ignore id: Option[String], scientificName: String, commonName: String, imageUrl: String)

case class Species(@Ignore id: Option[String], scientificName: String, commonName: String, imageUrl: String)

case class Culture(@Ignore id: Option[String], name: String, speciesUrl: Option[String], userUrl: Option[String], species: Option[Species] = None, projects: Option[List[Project]] = None)

case class Culture(@Ignore id: Option[String], name: String, speciesUrl: Option[String], userUrl: Option[String], species: Option[Species] = None, projects: Option[List[Project]] = None)

case class User(@Ignore id: Option[String], @Ignore dateCreated: Option[Date], @Ignore lastUpdated: Option[Date], email: String, password: String)

case class User(@Ignore id: Option[String], @Ignore dateCreated: Option[Date], @Ignore lastUpdated: Option[Date], email: String, password: String)