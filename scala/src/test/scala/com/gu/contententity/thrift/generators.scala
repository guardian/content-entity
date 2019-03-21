package com.gu.contententity

import com.gu.contententity.thrift.entity.film.Film
import com.gu.contententity.thrift.entity.game.Game
import com.gu.contententity.thrift.entity.organisation.Organisation
import com.gu.contententity.thrift.entity.person.Person
import com.gu.contententity.thrift.entity.place.Place
import com.gu.contententity.thrift.entity.restaurant.Restaurant

import org.scalacheck.{Arbitrary, Gen}

package object thrift {
    
    import Arbitrary.arbitrary

    implicit val arbEntityType: Arbitrary[EntityType] = Arbitrary {
        Gen.oneOf[EntityType]( EntityType.Person
            , EntityType.Film
            , EntityType.Game
            , EntityType.Restaurant
            , EntityType.Place
            , EntityType.Organisation
            )
    }

    implicit val arbPrice: Arbitrary[Price] = Arbitrary {
        for {
            currency <- arbitrary[String]
            value <- arbitrary[Int]
        } yield Price(currency, value)
    }

    implicit val arbGeolocation: Arbitrary[Geolocation] = Arbitrary {
        for {
            lat <- arbitrary[Double]
            lon <- arbitrary[Double] 
        } yield Geolocation(lat, lon)
    }

    implicit val arbAddress: Arbitrary[Address] = Arbitrary {
        for {
            formattedAddress <- arbitrary[Option[String]]
            streetNumber <- arbitrary[Option[Short]]
            streetName <- arbitrary[Option[String]]
            neighbourhood <- arbitrary[Option[String]]
            postTown <- arbitrary[Option[String]]
            locality <- arbitrary[Option[String]]
            country <- arbitrary[Option[String]]
            administrativeAreaLevelOne <- arbitrary[Option[String]]
            administrativeAreaLevelTwo <- arbitrary[Option[String]]
            postCode <- arbitrary[Option[String]]
        } yield Address(formattedAddress, streetNumber, streetName, neighbourhood, postTown, locality, country, administrativeAreaLevelOne, administrativeAreaLevelTwo, postCode)
    }

    implicit val arbPerson: Arbitrary[Person] = Arbitrary(arbitrary[String].map(Person(_)))

    implicit val arbFilm: Arbitrary[Film] = Arbitrary {
        for {
            title <- arbitrary[String]
            year <- arbitrary[Short]
            imdbId <- arbitrary[String]
            directors <- arbitrary[List[Person]]
            actors <- arbitrary[List[Person]]
            genre <- arbitrary[List[String]]
        } yield Film(title, year, imdbId, directors, actors, genre)
    }

    implicit val arbGame: Arbitrary[Game] = Arbitrary {
        for {
            title <- arbitrary[String]
            publisher <- arbitrary[Option[String]] 
            platforms <- arbitrary[List[String]] 
            price <- arbitrary[Option[Price]] 
            pegiRating <- arbitrary[Option[Int]] 
            genre <- arbitrary[List[String]]
        } yield Game(title, publisher, platforms, price, pegiRating, genre)
    }

    implicit val arbRestaurant: Arbitrary[Restaurant] = Arbitrary {
        for {
            restaurantName <- arbitrary[String]
            approximateLocation <- arbitrary[Option[String]]
            webAddress <- arbitrary[Option[String]]
            address <- arbitrary[Option[Address]]
            geolocation <- arbitrary[Option[Geolocation]]
        } yield Restaurant(restaurantName, approximateLocation, webAddress, address, geolocation)
    }

    implicit val arbPlace: Arbitrary[Place] = Arbitrary(arbitrary[String].map(Place(_)))

    implicit val arbOrg: Arbitrary[Organisation] = Arbitrary(arbitrary[String].map(Organisation(_)))

    implicit val arbEntity: Arbitrary[Entity] = Arbitrary {
        for {
            id <- arbitrary[String]
            googleId <- arbitrary[Option[String]]
            entityType <- arbitrary[EntityType]
            entity <- entityType match {
                case EntityType.Person => 
                    arbitrary[Person].map(p => Entity(id, entityType, googleId, person = Some(p)))
                case EntityType.Film =>
                    arbitrary[Film].map(f => Entity(id, entityType, googleId, film = Some(f)))
                case EntityType.Game =>
                    arbitrary[Game].map(g => Entity(id, entityType, googleId, game = Some(g)))
                case EntityType.Restaurant =>
                    arbitrary[Restaurant].map(r => Entity(id, entityType, googleId, restaurant = Some(r)))
                case EntityType.Place =>
                    arbitrary[Place].map(p => Entity(id, entityType, googleId, place = Some(p)))
                case EntityType.Organisation =>
                    arbitrary[Organisation].map(o => Entity(id, entityType, googleId, organisation = Some(o)))
                case _ =>
                    Gen.fail
            }
        } yield entity
    }
}