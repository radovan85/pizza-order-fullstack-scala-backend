package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import jakarta.validation.constraints.{NotEmpty, NotNull, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class ShippingAddressDto extends Serializable {

  @BeanProperty var shippingAddressId: Integer = _

  @NotEmpty
  @Size(min = 3, max = 75)
  @BeanProperty var address: String = _

  @NotEmpty
  @Size(min = 3, max = 40)
  @BeanProperty var city: String = _

  @NotEmpty
  @Size(min = 5, max = 10)
  @BeanProperty var postcode: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var customerId: Integer = _
}
