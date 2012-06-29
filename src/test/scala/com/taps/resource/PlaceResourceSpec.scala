package com.taps.resource

import org.specs2.mutable._
import cc.spray._
import test._
import http._
import HttpMethods._
import HttpHeaders._
import MediaTypes._
import StatusCodes._

class PlaceResourceSpec extends Specification with SprayTest with PlaceResource {
  "The Beer service" should {
    "be true" in {
      true must_== true
    }
  }
}
