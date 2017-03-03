namespace java com.gu.contententity.thrift

include "entities/person.thrift"
include "entities/film.thrift"
include "entities/game.thrift"
include "entities/restaurant.thrift"

enum EntityType {
  PERSON = 0
  FILM = 1
  GAME = 2
  RESTAURANT = 3
}

// A container for an entity
struct Entity {
  1: required string id         //Unique entity ID, e.g. person/theresa_may/12345
  2: required EntityType entityType
  3: optional string googleId   //Google Knowledge Graph ID

  4: optional person.Person person
  5: optional film.Film film
  6: optional game.Game game
  7: optional restaurant.Restaurant restaurant
}
