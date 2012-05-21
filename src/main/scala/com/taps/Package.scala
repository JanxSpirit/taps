package com.taps

import akka.util.Deadline._
import akka.util.Deadline
import java.util.Date
import util._

package object util {
  implicit def deadline2Date(dl: Deadline) = new Date(dl.time.toMillis)
}

package object model {
  implicit def user2UserWrapper(user: User): UserWrapper = 
    UserWrapper(user.id, 1, now, now, user)

  implicit def beer2Wrapper(beer: Beer): BeerWrapper = 
    BeerWrapper(beer.id, 1, now, now, beer)

  implicit def wrapper2Beer(wrapper: BeerWrapper): Beer = 
    wrapper.content.copy(id = wrapper._id)
 
  implicit def userWrapper2User(userWrapper: UserWrapper): User = 
    userWrapper.content.copy(id = userWrapper._id, 
			     dateCreated = Some(userWrapper.dateCreated), 
			     lastUpdated = Some(userWrapper.lastUpdated))
  
}

