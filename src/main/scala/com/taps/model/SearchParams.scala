package com.taps.model

import com.mongodb.casbah.Imports._

case class ProjectSearchParams(name: Option[String], description: Option[String], userUrl: Option[String])

case class ProjectSearchParams(name: Option[String], description: Option[String], userUrl: Option[String])

case class SpeciesSearchParams(scientificName: Option[String], commonName: Option[String])

case class SpeciesSearchParams(scientificName: Option[String], commonName: Option[String])

case class CultureSearchParams(name: Option[String], userUrl: Option[String])

case class CultureSearchParams(name: Option[String], userUrl: Option[String])

case class UserSearchParams(email: Option[String], password: Option[String])

case class UserSearchParams(email: Option[String], password: Option[String])