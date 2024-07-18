package com.radovan.spring.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.dto.OrderAddressDto
import com.radovan.spring.dto.OrderDto
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.dto.PizzaDto
import com.radovan.spring.dto.PizzaSizeDto
import com.radovan.spring.dto.UserDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.{CustomerService, OrderAddressService, OrderItemService, OrderService, PizzaService, PizzaSizeService, UserService}

@RestController
@RequestMapping(value = Array("/api/admin"))
class AdminController {

  private var pizzaService: PizzaService = _
  private var pizzaSizeService: PizzaSizeService = _
  private var orderService: OrderService = _
  private var userService: UserService = _
  private var customerService: CustomerService = _
  private var orderItemService: OrderItemService = _
  private var orderAddressService: OrderAddressService = _

  @Autowired
  private def injectAll(pizzaService: PizzaService, pizzaSizeService: PizzaSizeService, orderService: OrderService,
                        userService: UserService, customerService: CustomerService, orderItemService: OrderItemService,
                        orderAddressService: OrderAddressService): Unit = {
    this.pizzaService = pizzaService
    this.pizzaSizeService = pizzaSizeService
    this.orderService = orderService
    this.userService = userService
    this.customerService = customerService
    this.orderItemService = orderItemService
    this.orderAddressService = orderAddressService
  }

  @PostMapping(value = Array("/storePizza"))
  def storePizza(@Validated @RequestBody pizza: PizzaDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data is not validated"))
    }
    val storedPizza = pizzaService.addPizza(pizza)
    new ResponseEntity("The pizza with id " + storedPizza.getPizzaId + " has been stored!", HttpStatus.OK)
  }

  @PutMapping(value = Array("/updatePizza/{pizzaId}"))
  def updatePizza(@Validated @RequestBody pizza: PizzaDto, errors: Errors, @PathVariable("pizzaId") pizzaId: Integer): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data is not validated"))
    }
    val updatedPizza = pizzaService.updatePizza(pizzaId, pizza)
    new ResponseEntity("The product with id " + updatedPizza.getPizzaId + " has been updated without any issues", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deletePizza/{pizzaId}"))
  def deletePizza(@PathVariable("pizzaId") pizzaId: Integer): ResponseEntity[String] = {
    pizzaService.deletePizza(pizzaId)
    new ResponseEntity("The pizza with id " + pizzaId + " has been permanently deleted!", HttpStatus.OK)
  }

  @PostMapping(value = Array("/storePizzaSize"))
  def storePizzaSize(@Validated @RequestBody pizzaSize: PizzaSizeDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data is not validated!"))
    }
    val storedSize = pizzaSizeService.addPizzaSize(pizzaSize)
    new ResponseEntity("Pizza size with id " + storedSize.getPizzaSizeId + " has been stored!", HttpStatus.OK)
  }

  @PutMapping(value = Array("/updatePizzaSize/{sizeId}"))
  def updatePizzaSize(@Validated @RequestBody pizzaSize: PizzaSizeDto, errors: Errors, @PathVariable("sizeId") sizeId: Integer): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data is not validated!"))
    }
    val updatedSize = pizzaSizeService.updatePizzaSize(sizeId, pizzaSize)
    new ResponseEntity("Pizza size with id " + updatedSize.getPizzaSizeId + " has been updated without any issues!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deletePizzaSize/{sizeId}"))
  def deletePizzaSize(@PathVariable("sizeId") sizeId: Integer): ResponseEntity[String] = {
    pizzaSizeService.deletePizzaSize(sizeId)
    new ResponseEntity("Pizza size with id " + sizeId + " has been permanently deleted!", HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteOrder/{orderId}"))
  def deleteOrder(@PathVariable("orderId") orderId: Integer): ResponseEntity[String] = {
    orderService.deleteOrder(orderId)
    new ResponseEntity("Order with id " + orderId + " has been permanently deleted!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/allOrders"))
  def getAllOrders: ResponseEntity[Array[OrderDto]] = {
    val allOrders = orderService.listAll
    new ResponseEntity(allOrders, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allUsers"))
  def getAllUsers: ResponseEntity[Array[UserDto]] = {
    val allUsers = userService.listAll
    new ResponseEntity(allUsers, HttpStatus.OK)
  }

  @GetMapping(value = Array("/suspendUser/{userId}"))
  def suspendUser(@PathVariable("userId") userId: Integer): ResponseEntity[String] = {
    userService.suspendUser(userId)
    new ResponseEntity("The user with id " + userId + " has been suspended!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/reactivateUser/{userId}"))
  def reactivateUser(@PathVariable("userId") userId: Integer): ResponseEntity[String] = {
    userService.reactivateUser(userId)
    new ResponseEntity("The user with id " + userId + " has been reactivated!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/allCustomers"))
  def getAllCustomers: ResponseEntity[Array[CustomerDto]] = {
    val allCustomers = customerService.listAll
    new ResponseEntity(allCustomers, HttpStatus.OK)
  }

  @DeleteMapping(value = Array("/deleteCustomer/{customerId}"))
  def deleteCustomer(@PathVariable("customerId") customerId: Integer): ResponseEntity[String] = {
    customerService.removeCustomer(customerId)
    new ResponseEntity("The customer with id " + customerId + "has been permanently deleted!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/allItems/{orderId}"))
  def listAllByOrderId(@PathVariable("orderId") orderId: Integer): ResponseEntity[Array[OrderItemDto]] = {
    val allItems = orderItemService.listAllByOrderId(orderId)
    new ResponseEntity(allItems, HttpStatus.OK)
  }

  @GetMapping(value = Array("/orderDetails/{orderId}"))
  def getOrderDetails(@PathVariable("orderId") orderId: Integer): ResponseEntity[OrderDto] = {
    val order = orderService.getOrderById(orderId)
    new ResponseEntity(order, HttpStatus.OK)
  }

  @GetMapping(value = Array("/orderAddress/{orderId}"))
  def getOrderAddress(@PathVariable("orderId") orderId: Integer): ResponseEntity[OrderAddressDto] = {
    val order = orderService.getOrderById(orderId)
    val address = orderAddressService.getAddressById(order.getAddressId)
    new ResponseEntity(address, HttpStatus.OK)
  }

  @GetMapping(value = Array("/customerDetails/{customerId}"))
  def getCustomerDetails(@PathVariable("customerId") customerId: Integer): ResponseEntity[CustomerDto] = {
    val customer = customerService.getCustomerById(customerId)
    new ResponseEntity(customer, HttpStatus.OK)
  }
}
