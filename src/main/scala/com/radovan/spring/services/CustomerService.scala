package com.radovan.spring.services

import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.utils.RegistrationForm

trait CustomerService {

  def addCustomer(form:RegistrationForm):CustomerDto
  def getCustomerById(customerId:Integer):CustomerDto
  def getCustomerByUserId(userId:Integer):CustomerDto
  def listAll:Array[CustomerDto]
  def getCurrentCustomer:CustomerDto
  def updateCustomer(customer:CustomerDto):CustomerDto
  def removeCustomer(customerId:Integer):Unit
}
