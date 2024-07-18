package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class OrderDto extends Serializable {

  @BeanProperty var orderId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var orderPrice: Float = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var cartId:Integer  = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var createTime: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var orderedItemsIds: Array[Integer] = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var addressId: Integer = _
}
