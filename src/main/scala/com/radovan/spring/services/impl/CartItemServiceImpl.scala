package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.{CartItemDto, CustomerDto}
import com.radovan.spring.entity.CartItemEntity
import com.radovan.spring.exceptions.{InstanceUndefinedException, InvalidCartException, OperationNotAllowedException}
import com.radovan.spring.repositories.CartItemRepository
import com.radovan.spring.services.{CartItemService, CartService, CustomerService, PizzaSizeService, UserService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
class CartItemServiceImpl extends CartItemService {

  private var cartItemRepository: CartItemRepository = _
  private var cartService: CartService = _
  private var customerService: CustomerService = _
  private var pizzaSizeService: PizzaSizeService = _
  private var userService: UserService = _
  private var tempConverter: TempConverter = _

  @Transactional
  override def addCartItem(cartItem: CartItemDto): CartItemDto = {
    pizzaSizeService.getPizzaSizeById(cartItem.getPizzaSizeId)
    var returnValue: CartItemDto = null
    var cartItemEntity: CartItemEntity = null
    val customer = customerService.getCurrentCustomer
    cartItem.setCartId(customer.getCartId)
    val allCartItems = listAllByCartId(customer.getCartId)
    if (allCartItems.nonEmpty) {
      var quantity = cartItem.getQuantity
      var cartQuantity = 0
      allCartItems.foreach(item => cartQuantity = cartQuantity + item.getQuantity)
      if (cartQuantity + quantity > 20) {
        throw new InvalidCartException(new Error("Maximum 20 pizzas allowed in the cart!"))
      }
      allCartItems.foreach(item => {
        if (item.getPizzaSizeId == cartItem.getPizzaSizeId) {
          quantity = quantity + item.getQuantity
          cartItem.setCartItemId(item.getCartItemId)
        }
      })

      cartItem.setQuantity(quantity)
      cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
      val updatedItem = cartItemRepository.saveAndFlush(cartItemEntity)
      cartService.refreshCartState(customer.getCartId)
      returnValue = tempConverter.cartItemEntityToDto(updatedItem)
    } else {
      cartItemEntity = tempConverter.cartItemDtoToEntity(cartItem)
      val storedItem = cartItemRepository.save(cartItemEntity)
      cartService.refreshCartState(customer.getCartId)
      returnValue = tempConverter.cartItemEntityToDto(storedItem)
    }

    returnValue
  }

  @Transactional
  override def removeCartItem(itemId: Integer): Unit = {
    val cartItem = getItemById(itemId)
    val currentCustomer = customerService.getCurrentCustomer
    val cartId = currentCustomer.getCartId
    if (cartId == cartItem.getCartId) {
      cartItemRepository.eraseCartItem(itemId)
      cartItemRepository.flush()
      cartService.refreshCartState(cartId)
    } else {
      throw new OperationNotAllowedException(new Error("Operation not allowed!"))
    }
  }

  @Transactional
  override def eraseAllCartItems(cartId: Integer): Unit = {
    val customerOpt: Option[CustomerDto] = Option(customerService.getCurrentCustomer)
    customerOpt match {
      case Some(customer) =>
        if (customer.getCartId == cartId) {
          cartItemRepository.deleteAllByCartId(cartId)
          cartItemRepository.flush()
          cartService.refreshCartState(cartId)
        } else {
          throw new OperationNotAllowedException(new Error("Operation not allowed!"))
        }
      case None =>
        if (userService.isAdmin) {
          cartItemRepository.deleteAllByCartId(cartId)
          cartItemRepository.flush()
          cartService.refreshCartState(cartId)
        }
    }

  }

  @Transactional
  override def eraseAllByPizzaSizeId(pizzaSizeId: Integer): Unit = {
    pizzaSizeService.getPizzaSizeById(pizzaSizeId)
    cartItemRepository.deleteAllByPizzaSizeId(pizzaSizeId)
    cartItemRepository.flush()
    cartService.refreshAllCarts()
  }

  @Transactional(readOnly = true)
  override def listAllByPizzaSizeId(pizzaSizeId: Integer): Array[CartItemDto] = {
    pizzaSizeService.getPizzaSizeById(pizzaSizeId)
    val allItems = cartItemRepository.findAllByPizzaSizeId(pizzaSizeId).asScala
    allItems.map(itemEntity => tempConverter.cartItemEntityToDto(itemEntity)).toArray
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): Array[CartItemDto] = {
    cartService.getCartById(cartId)
    val allItems = cartItemRepository.findAllByCartId(cartId).asScala
    allItems.map(itemEntity => tempConverter.cartItemEntityToDto(itemEntity)).toArray
  }

  @Transactional(readOnly = true)
  override def getItemById(itemId: Integer): CartItemDto = {
    val cartItem = cartItemRepository.findById(itemId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The item has not been found!")))
    tempConverter.cartItemEntityToDto(cartItem)
  }

  @Autowired
  private def injectAll(cartItemRepository: CartItemRepository, cartService: CartService, customerService: CustomerService,
                        pizzaSizeService: PizzaSizeService, userService: UserService, tempConverter: TempConverter): Unit = {

    this.cartItemRepository = cartItemRepository
    this.cartService = cartService
    this.customerService = customerService
    this.pizzaSizeService = pizzaSizeService
    this.userService = userService
    this.tempConverter = tempConverter
  }
}
