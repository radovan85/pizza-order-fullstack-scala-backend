package com.radovan.spring.controllers

import com.radovan.spring.services.{CartService, CustomerService, OrderService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("/api/order"))
class OrderController {

  private var orderService:OrderService = _
  private var customerService:CustomerService = _
  private var cartService:CartService = _

  @Autowired
  private def injectAll(orderService: OrderService,customerService: CustomerService,cartService: CartService):Unit = {
    this.orderService = orderService
    this.customerService = customerService
    this.cartService = cartService
  }

  @PostMapping(value = Array("/createOrder"))
  def createOrder: ResponseEntity[String] = {
    orderService.addOrder()
    new ResponseEntity("Order completed!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/checkout"))
  def checkout: ResponseEntity[String] = {
    val customer = customerService.getCurrentCustomer
    cartService.validateCart(customer.getCartId)
    new ResponseEntity("Ckeckout is processing...", HttpStatus.OK)
  }
}
