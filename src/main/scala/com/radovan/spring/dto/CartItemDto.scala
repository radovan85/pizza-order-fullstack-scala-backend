package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class CartItemDto extends Serializable {

  @BeanProperty var cartItemId: Integer = _

  @NotNull
  @Range(min = 1, max = 20)
  @BeanProperty var quantity: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var price: Float = _

  @NotNull
  @BeanProperty var pizzaSizeId: Integer = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var cartId: Integer = _
}
