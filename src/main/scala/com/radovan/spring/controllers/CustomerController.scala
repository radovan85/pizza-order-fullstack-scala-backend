package com.radovan.spring.controllers

import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.services.{CustomerService, ShippingAddressService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = Array("/api/customers"))
class CustomerController {

  private var customerService:CustomerService = _
  private var shippingAddressService:ShippingAddressService = _

  @Autowired
  private def injectAll(customerService: CustomerService,shippingAddressService: ShippingAddressService):Unit = {
    this.customerService = customerService
    this.shippingAddressService = shippingAddressService
  }

  @GetMapping(value = Array("/getMyAddress"))
  def getMyAddress: ResponseEntity[ShippingAddressDto] = {
    val customer = customerService.getCurrentCustomer
    val address = shippingAddressService.getAddressById(customer.getShippingAddressId)
    new ResponseEntity(address, HttpStatus.OK)
  }

  @GetMapping(value = Array("/currentCustomer"))
  def getCurrentCustomer: ResponseEntity[CustomerDto] = {
    val customer = customerService.getCurrentCustomer
    new ResponseEntity(customer, HttpStatus.OK)
  }

  @PutMapping(value = Array("/updateCustomer"))
  def updateCustomer(@Validated @RequestBody customer: CustomerDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated!"))
    }
    val updatedCustomer = customerService.updateCustomer(customer)
    new ResponseEntity("The customer with id: " + updatedCustomer.getCustomerId + " has been updated without any issues!", HttpStatus.OK)
  }

  @PutMapping(value = Array("/updateShippingAddress/{addressId}"))
  def updateShippingAddress(@PathVariable("addressId") addressId: Integer, @RequestBody address: ShippingAddressDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated!"))
    }
    shippingAddressService.updateShippingAddress(addressId, address)
    new ResponseEntity("The address has been updated without any issues", HttpStatus.OK)
  }
}
