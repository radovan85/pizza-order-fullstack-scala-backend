package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class OrderItemDto extends Serializable {

  @BeanProperty var orderItemId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var quantity: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var price: Float = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var pizza: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var pizzaSize: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var pizzaPrice: Float = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var orderId: Integer = _
}
