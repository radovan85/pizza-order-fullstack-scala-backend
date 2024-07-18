package com.radovan.spring.services

import com.radovan.spring.dto.OrderDto

trait OrderService {

  def addOrder():OrderDto
  def listAll:Array[OrderDto]
  def listAllByCartId(cartId:Integer):Array[OrderDto]
  def calculateOrderTotal(orderId:Integer):Float
  def getOrderById(orderId:Integer):OrderDto
  def deleteOrder(orderId:Integer):Unit
}
