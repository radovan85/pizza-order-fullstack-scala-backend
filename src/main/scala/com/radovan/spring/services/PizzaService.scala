package com.radovan.spring.services

import com.radovan.spring.dto.PizzaDto

trait PizzaService {

  def listAll:Array[PizzaDto]
  def getPizzaById(pizzaId:Integer):PizzaDto
  def deletePizza(pizzaId:Integer):Unit
  def addPizza(pizza:PizzaDto):PizzaDto
  def updatePizza(pizzaId:Integer,pizza:PizzaDto):PizzaDto
}
