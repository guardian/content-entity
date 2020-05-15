namespace java com.gu.contententity.thrift.entity.restaurant
#@namespace typescript _at_guardian.content_entity_model.restaurant

include "../shared.thrift"

struct Restaurant {
  1: required string restaurantName
  2: optional string approximateLocation
  3: optional string webAddress
  4: optional shared.Address address
  5: optional shared.Geolocation geolocation
}