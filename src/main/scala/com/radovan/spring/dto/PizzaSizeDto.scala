package com.radovan.spring.dto

import jakarta.validation.constraints.{DecimalMin, NotEmpty, NotNull, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class PizzaSizeDto extends Serializable {

  @BeanProperty var pizzaSizeId: Integer = _

  @NotEmpty
  @Size(min = 3, max = 40)
  @BeanProperty var name: String = _

  @NotNull
  @DecimalMin(value = "1.00")
  @BeanProperty var price: Float = _

  @NotNull
  @BeanProperty var pizzaId: Integer = _
}
