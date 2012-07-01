package com.taps.resource

import com.taps.dao.PlaceDao
import com.taps.TestData
import org.specs2.mutable._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.{write => jsonWrite}
import cc.spray._
import test._
import http._
import HttpMethods._
import HttpHeaders._
import MediaTypes._
import StatusCodes._

class PlaceResourceSpec extends Specification 
			with SprayTest 
			with TapsSpec 
			with PlaceResource 
			with TestData {

  override def resourceName = "places"
  override def service = new PlaceDao {
    override val mongoCollection = configDb
    override val actorSystem = system
  }

  "The Place resource" should {
    "save a place" in {
      testService(HttpRequest(method=POST, 
			      uri=resourceName,
			      content=Some(HttpContent(jsonWrite(testPlace))))) {
	restService
      }.response.status mustEqual 201
    }
  }
}
