package com.taps.service
import akka.dispatch.Future

import com.taps.dao.{BeerDao, BreweryDao, UserDao}
import com.taps.model.Beer

trait UserBeerService {
  val beerDao: BeerDao
  val breweryDao: BreweryDao
  val userDao: UserDao

  implicit val actorSystem = beerDao.actorSystem

  //this wants to convert - List[String] => List[Option[Beer]]
  def getFavBeers(userId: String): Future[List[Option[Beer]]] = 
    userDao.getByKey(userId).flatMap {
      case Some(u) => Future.sequence(u.favBeers.map(id => beerDao.getByKey(id)))
    }
	
  def getUnfavBeers: Seq[Beer]
  def getFavBreweries: Seq[Beer]
  def getUnfavBreweries: Seq[Beer]

}
