package com.taps.service

import com.taps.dao.{PlaceBeerDao, PlaceDao, UserDao}

trait UserPlaceService {
  val userDao: UserDao
  val placeDao: PlaceDao
  val placeBeerDao: PlaceBeerDao

 
}
