package com.taps.service

import akka.dispatch.Future
import com.taps.dao.{BeerDao, PlaceDao}
import com.taps.model.{Beer, Place, PlaceBeer}

trait PlaceBeerService {
  
  val placeDao: PlaceDao
  val beerDao: BeerDao

  def updatePlaceBeer(placeBeer: PlaceBeer) = {
    //verify beer exists
    //see if beer is already at place
    //add or update as necessary
  }
}
