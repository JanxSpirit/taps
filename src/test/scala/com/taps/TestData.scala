package com.taps

import com.taps.model._

trait TestData {
  def testPlace = 
    Place(id = None,
	  description = Some("A test place"),
	  name = "Test Place",
	  location = Some(testLocation),
	  website = Some("http://testplacesite"),
	  None,
	  None)

  def testLocation = 
    Location(city = Some("New York City"),
	     state = Some("New York"),
	     country = Some("United States"),
	     streetAddress = Some("123 Broadway Ave"),
	     zip = Some(01234),
	     lat = Some(40.7142),
	     lon = Some(74.0064),
	     latlon = Some((40.7142, 74.0064)))
}
