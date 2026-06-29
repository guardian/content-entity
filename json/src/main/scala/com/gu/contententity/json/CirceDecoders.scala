package com.gu.contententity.json

import com.gu.contententity.thrift.entity.film.Film
import com.gu.contententity.thrift.entity.game.Game
import com.gu.contententity.thrift.entity.organisation.Organisation
import com.gu.contententity.thrift.entity.person.Person
import com.gu.contententity.thrift.entity.place.Place
import com.gu.contententity.thrift.entity.restaurant.Restaurant
import com.gu.contententity.thrift._
import com.gu.fezziwig.CirceScroogeMacros._
import com.gu.fezziwig.CirceScroogeWhiteboxMacros._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object CirceDecoders {
  implicit lazy val filmDecoder: Decoder[Film] = deriveDecoder
  implicit lazy val gameDecoder: Decoder[Game] = deriveDecoder
  implicit lazy val organisationDecoder: Decoder[Organisation] = deriveDecoder
  implicit lazy val personDecoder: Decoder[Person] = deriveDecoder
  implicit lazy val placeDecoder: Decoder[Place] = deriveDecoder
  implicit lazy val restaurantDecoder: Decoder[Restaurant] = deriveDecoder

  implicit lazy val addressDecoder: Decoder[Address] = deriveDecoder
  implicit lazy val geolocationDecoder: Decoder[Geolocation] = deriveDecoder
  implicit lazy val priceDecoder: Decoder[Price] = deriveDecoder

  implicit lazy val entityTypeDecoder: Decoder[EntityType] = deriveDecoder
  implicit lazy val entityDecoder: Decoder[Entity] = deriveDecoder
}
