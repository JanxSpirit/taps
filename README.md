taps
====

In SBT console run this command to start container:

re-start

You can set it to constantly build/deploy with:

~re-start

Or if you have JRebel installed you can do:

~products

Stuff that needs to work
========================

Add a beer to a place

Add a favorite

Add a place

Find places nearby with my favorites

API
===

/beers - POST

/beers/{id} - GET, PUT

/places - POST

/places/{id} - GET, PUT

/places/{id}/beers - POST

/places/{id}/beers/{id} - GET, PUT

/breweries - POST

/breweries/{id} - GET, PUT

/breweries/{id}/beers/ - POST

/breweries/{id}/beers/{id} - GET, PUT

/users - POST

/users/{id} - GET, PUT

/users/{id}/(favBeers | unfavBeers | favBreweries | unfavBreweries) - POST, GET

/users/{id}/(favBeers | unfavBeers | favBreweries | unfavBreweries)/{id} - DELETE