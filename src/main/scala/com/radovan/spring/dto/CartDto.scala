package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CartDto extends Serializable {

  @BeanProperty var cartId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var customerId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var cartItemsIds: Array[Integer] = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var cartPrice: Float = _
}
