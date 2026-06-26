package com.gu.contententity.json

import com.gu.contententity.thrift.{Address, EntityType, Geolocation, Price}
import com.gu.contententity.thrift.entity.film.Film
import com.gu.contententity.thrift.entity.game.Game
import com.gu.contententity.thrift.entity.organisation.Organisation
import com.gu.contententity.thrift.entity.person.Person
import com.gu.contententity.thrift.entity.place.Place
import com.gu.contententity.thrift.entity.restaurant.Restaurant
import com.gu.fezziwig.CirceScroogeMacros._
import com.gu.fezziwig.CirceScroogeWhiteboxMacros._
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object CirceEncoders {
  implicit lazy val filmEncoder: Encoder[Film] = deriveEncoder
  implicit lazy val gameEncoder: Encoder[Game] = deriveEncoder
  implicit lazy val organisationEncoder: Encoder[Organisation] = deriveEncoder
  implicit lazy val personEncoder: Encoder[Person] = deriveEncoder
  implicit lazy val placeEncoder: Encoder[Place] = deriveEncoder
  implicit lazy val restaurantEncoder: Encoder[Restaurant] = deriveEncoder

  implicit lazy val entityTypeEncoder: Encoder[EntityType] = deriveEncoder

  implicit lazy val addressEncoder: Encoder[Address] = deriveEncoder
  implicit lazy val geolocationEncoder: Encoder[Geolocation] = deriveEncoder
  implicit lazy val priceEncoder: Encoder[Price] = deriveEncoder
}
