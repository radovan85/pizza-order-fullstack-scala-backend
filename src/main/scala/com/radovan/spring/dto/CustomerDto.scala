package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import jakarta.validation.constraints.{NotEmpty, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CustomerDto extends Serializable {

  @BeanProperty var customerId: Integer = _

  @NotEmpty
  @Size(min = 9, max = 15)
  @BeanProperty var customerPhone: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var shippingAddressId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var userId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var cartId: Integer = _
}
