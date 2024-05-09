package com.radovan.spring.entity

import jakarta.persistence.{CascadeType, Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, OneToMany, OneToOne, Table}
import org.hibernate.annotations.{Fetch, FetchMode}

import java.util
import scala.beans.BeanProperty

@Entity
@Table(name = "carts")
@SerialVersionUID(1L)
class CartEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty var cartId:Integer = _

  @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "customer_id", insertable = false)
  @BeanProperty var customer:CustomerEntity = _

  @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, orphanRemoval = true, cascade = Array(CascadeType.ALL))
  @Fetch(value = FetchMode.SUBSELECT)
  @BeanProperty var cartItems: util.List[CartItemEntity] = _

  @Column(name = "cart_price", nullable = false)
  @BeanProperty var cartPrice: Float = _
}
