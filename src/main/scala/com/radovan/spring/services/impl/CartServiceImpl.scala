package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CartDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, InvalidCartException}
import com.radovan.spring.repositories.{CartItemRepository, CartRepository}
import com.radovan.spring.services.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import scala.collection.JavaConverters._

@Service
class CartServiceImpl extends CartService{

  private var cartRepository:CartRepository = _
  private var cartItemRepository:CartItemRepository = _
  private var tempConverter:TempConverter = _
  private val decfor = new DecimalFormat("0.00")

  @Transactional(readOnly = true)
  override def getCartById(cartId: Integer): CartDto = {
    val cart = cartRepository.findById(cartId).orElseThrow(() => new InstanceUndefinedException(new Error("The cart has not been found!")))
    tempConverter.cartEntityToDto(cart)
  }

  @Transactional(readOnly = true)
  override def validateCart(cartId: Integer): CartDto = {
    val cart = getCartById(cartId)
    if(cart.getCartItemsIds.isEmpty){
      throw new InvalidCartException(new Error("The cart is empty!"))
    }
    cart
  }

  @Transactional(readOnly = true)
  override def calculateGrandTotal(cartId: Integer): Float = {
    val grandTotalOpt = cartItemRepository.calculateGrandTotal(cartId)
    grandTotalOpt match {
      case Some(grandTotal) =>
        decfor.format(grandTotal).toFloat
      case None => 0f
    }
  }

  @Transactional
  override def refreshCartState(cartId: Integer): Unit = {
    val cart = getCartById(cartId)
    cart.setCartPrice(calculateGrandTotal(cart.getCartId))
    cartRepository.saveAndFlush(tempConverter.cartDtoToEntity(cart))
  }

  @Transactional
  override def refreshAllCarts(): Unit = {
    val allCarts = cartRepository.findAll().asScala
    allCarts.foreach(cartEntity => refreshCartState(cartEntity.getCartId))
  }

  @Autowired
  private def injectAll(cartRepository: CartRepository,cartItemRepository: CartItemRepository,tempConverter: TempConverter):Unit = {
    this.cartRepository = cartRepository
    this.cartItemRepository = cartItemRepository
    this.tempConverter = tempConverter
  }
}
