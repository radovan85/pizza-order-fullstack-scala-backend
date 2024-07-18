package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class OrderAddressDto extends Serializable {

  @BeanProperty var orderAddressId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var address: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var city: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var postcode: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var orderId: Integer = _
}
