package com.taps.service
import akka.dispatch.Future

import com.taps.dao.{BeerDao, BreweryDao, UserDao}
import com.taps.model.{Beer, Brewery}

trait UserBeerService {
  val beerDao: BeerDao
  val breweryDao: BreweryDao
  val userDao: UserDao

  implicit val actorSystem = beerDao.actorSystem

  //this wants to convert - List[String] => List[Option[Beer]]
  def getFavBeers(userId: String): Future[List[Option[Beer]]] = 
    userDao.getByKey(userId).flatMap {
      case Some(u) => Future.sequence(u.favBeers.map(id => beerDao.getByKey(id)))
      case None => Future{Nil}
    }
	
  def getUnfavBeers(userId: String): Future[List[Option[Beer]]] = 
    userDao.getByKey(userId).flatMap {
      case Some(u) => Future.sequence(u.unfavBeers.map(id => beerDao.getByKey(id)))
      case None => Future{Nil}
    }

  def getFavBreweries(userId: String): Future[List[Option[Brewery]]] = 
    userDao.getByKey(userId).flatMap {
      case Some(u) => Future.sequence(u.favBreweries.map(id => breweryDao.getByKey(id)))
      case None => Future{Nil}
    }

  def getUnfavBreweries(userId: String): Future[List[Option[Brewery]]] = 
    userDao.getByKey(userId).flatMap {
      case Some(u) => Future.sequence(u.unfavBreweries.map(id => breweryDao.getByKey(id)))
      case None => Future{Nil}
    }

  def addFavBeer(userId: String, beerId: String): Future[Option[Beer]]

  def removeFavBeer(userId: String, beerId: String): Future[Option[Beer]]

  def addUnfavBeer(userId: String, beerId: String): Future[Option[Beer]]

  def removeUnfavBeer(userId: String, beerId: String): Future[Option[Beer]]

  def addFavBrewery(userId: String, breweryId: String): Future[Option[Brewery]]

  def removeFavBrewery(userId: String, breweryId: String): Future[Option[Brewery]]

  def addUnfavBrewery(userId: String, breweryId: String): Future[Option[Brewery]]

  def removeUnfavBrewery(userId: String, breweryId: String): Future[Option[Brewery]]
}
