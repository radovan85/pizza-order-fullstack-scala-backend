package com.radovan.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import jakarta.validation.constraints.{NotEmpty, Size}

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class PizzaDto extends Serializable {

  @BeanProperty var pizzaId: Integer = _

  @NotEmpty
  @Size(min =3, max = 40)
  @BeanProperty var name: String = _

  @NotEmpty
  @Size(min = 3, max = 90)
  @BeanProperty var description: String = _

  @JsonProperty(access = Access.READ_ONLY)
  @BeanProperty var pizzaSizesIds: Array[Integer] = _

  @NotEmpty
  @Size(min = 5, max = 255)
  @BeanProperty var imageUrl: String = _
}
