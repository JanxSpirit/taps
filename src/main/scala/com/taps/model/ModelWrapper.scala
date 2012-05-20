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

  object BeerWrapper {

    implicit def wrapper2Beer(wrapper: BeerWrapper): Beer = {
      wrapper.content.copy(id = wrapper._id)
    }
  }

  case class UserWrapper(_id: Option[String],
                         version: Long,
                         dateCreated: Date,
                         lastUpdated: Date,
                         content: User)
  object UserWrapper {

    implicit def userWrapper2User(userWrapper: UserWrapper): User = {
      userWrapper.content.copy(id = userWrapper._id, dateCreated = Some(userWrapper.dateCreated), lastUpdated = Some(userWrapper.lastUpdated))
    }
  }


