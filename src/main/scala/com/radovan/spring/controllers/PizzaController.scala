package com.radovan.spring.controllers

import com.radovan.spring.dto.PizzaDto
import com.radovan.spring.dto.PizzaSizeDto
import com.radovan.spring.services.PizzaService
import com.radovan.spring.services.PizzaSizeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = Array("/api/pizza"))
class PizzaController {

   private var pizzaService:PizzaService = _
   private var pizzaSizeService:PizzaSizeService = _

  @Autowired
  private def injectAll(pizzaService: PizzaService, pizzaSizeService: PizzaSizeService):Unit = {
    this.pizzaService = pizzaService
    this.pizzaSizeService = pizzaSizeService
  }

  @GetMapping(value = Array("/allPizzas"))
  def getAllPizzas: ResponseEntity[Array[PizzaDto]] = {
    val allPizzas = pizzaService.listAll
    new ResponseEntity(allPizzas, HttpStatus.OK)
  }


  @GetMapping(value = Array("/allPizzaSizes"))
  def getAllPizzaSizes: ResponseEntity[Array[PizzaSizeDto]] = {
    val allPizzaSizes = pizzaSizeService.listAll
    new ResponseEntity(allPizzaSizes, HttpStatus.OK)
  }

  @GetMapping(value = Array("/pizzaDetails/{pizzaId}"))
  def getPizzaDetails(@PathVariable("pizzaId") pizzaId: Integer): ResponseEntity[PizzaDto] = {
    val pizza = pizzaService.getPizzaById(pizzaId)
    new ResponseEntity(pizza, HttpStatus.OK)
  }

  @GetMapping(value = Array("/pizzaSizeDetails/{sizeId}"))
  def getPizzaSizeDetails(@PathVariable("sizeId") sizeId: Integer): ResponseEntity[PizzaSizeDto] = {
    val pizzaSize = pizzaSizeService.getPizzaSizeById(sizeId)
    new ResponseEntity(pizzaSize, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allPizzaSizes/{pizzaId}"))
  def allPizzaSizesByPizza(@PathVariable("pizzaId") pizzaId: Integer): ResponseEntity[Array[PizzaSizeDto]] = {
    val allPizzaSizes = pizzaSizeService.listAllByPizzaId(pizzaId)
    new ResponseEntity(allPizzaSizes, HttpStatus.OK)
  }
}