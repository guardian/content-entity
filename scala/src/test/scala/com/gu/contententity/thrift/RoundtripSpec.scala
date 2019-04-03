package com.gu.contententity.thrift

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object RoundtripSpec extends Properties("Roundtrip encoding/decoding") with ThriftSerializer {
    property("transport protocol") = forAll { (e: Entity) =>
        deserialize(serializeToBytes(e), Entity) == e
    }
}