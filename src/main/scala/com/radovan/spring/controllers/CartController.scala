package com.radovan.spring.controllers

import com.radovan.spring.dto.CartDto
import com.radovan.spring.dto.CartItemDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.{CartItemService, CartService, CustomerService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = Array("/api/cart"))
class CartController {

  private var cartItemService: CartItemService = _
  private var customerService: CustomerService = _
  private var cartService: CartService = _

  @Autowired
  private def injectAll(cartItemService: CartItemService, customerService: CustomerService, cartService: CartService): Unit = {
    this.cartItemService = cartItemService
    this.customerService = customerService
    this.cartService = cartService
  }

  @PostMapping(value = Array("/addCartItem"))
  def addCartItem(@Validated @RequestBody cartItem: CartItemDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data is not validated!"))
    }
    cartItemService.addCartItem(cartItem)
    new ResponseEntity("The item has been placed in the cart", HttpStatus.OK)
  }

  @GetMapping(value = Array("/getMyCart"))
  def getMyItems: ResponseEntity[Array[CartItemDto]] = {
    val currentCustomer = customerService.getCurrentCustomer
    val allCartItems = cartItemService.listAllByCartId(currentCustomer.getCartId)
    new ResponseEntity(allCartItems, HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/clearCart"))
  def clearCart: ResponseEntity[String] = {
    val customer = customerService.getCurrentCustomer
    cartItemService.eraseAllCartItems(customer.getCartId)
    new ResponseEntity("All cart items have been removed!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/removeCartItem/{itemId}"))
  def deleteItem(@PathVariable("itemId") itemId: Integer): ResponseEntity[String] = {
    cartItemService.removeCartItem(itemId)
    new ResponseEntity("The cart item has been removed!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/getCartById/{cartId}"))
  def getCartById(@PathVariable("cartId") cartId: Integer): ResponseEntity[CartDto] = {
    val cart = cartService.getCartById(cartId)
    new ResponseEntity(cart, HttpStatus.OK)
  }
}
