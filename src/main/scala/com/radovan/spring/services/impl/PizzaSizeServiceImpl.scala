package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.PizzaSizeDto
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repositories.{CartItemRepository, PizzaSizeRepository}
import com.radovan.spring.services.{CartItemService, CartService, PizzaService, PizzaSizeService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
class PizzaSizeServiceImpl extends PizzaSizeService {

  private var pizzaSizeRepository: PizzaSizeRepository = _
  private var pizzaService: PizzaService = _
  private var cartItemService: CartItemService = _
  private var cartItemRepository: CartItemRepository = _
  private var cartService: CartService = _
  private var tempConverter: TempConverter = _

  @Transactional
  override def addPizzaSize(pizzaSize: PizzaSizeDto): PizzaSizeDto = {
    pizzaService.getPizzaById(pizzaSize.getPizzaId)
    val pizzaSizeOption = pizzaSizeRepository.findByNameAndPizzaId(pizzaSize.getName, pizzaSize.getPizzaId)
    pizzaSizeOption match {
      case Some(_) => throw new ExistingInstanceException(new Error("Pizza size already exists!"))
      case None =>
    }

    val storedSize = pizzaSizeRepository.save(tempConverter.pizzaSizeDtoToEntity(pizzaSize))
    tempConverter.pizzaSizeEntityToDto(storedSize)
  }

  @Transactional(readOnly = true)
  override def getPizzaSizeById(pizzaSizeId: Integer): PizzaSizeDto = {
    val pizzaSizeEntity = pizzaSizeRepository.findById(pizzaSizeId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The pizza size has not been found!")))
    tempConverter.pizzaSizeEntityToDto(pizzaSizeEntity)
  }

  @Transactional
  override def deletePizzaSize(pizzaSizeId: Integer): Unit = {
    getPizzaSizeById(pizzaSizeId)
    cartItemService.eraseAllByPizzaSizeId(pizzaSizeId)
    pizzaSizeRepository.deleteById(pizzaSizeId)
    pizzaSizeRepository.flush()
  }

  @Transactional(readOnly = true)
  override def listAll: Array[PizzaSizeDto] = {
    val allSizes = pizzaSizeRepository.findAll().asScala
    allSizes.map(size => tempConverter.pizzaSizeEntityToDto(size)).toArray
  }

  @Transactional(readOnly = true)
  override def listAllByPizzaId(pizzaId: Integer): Array[PizzaSizeDto] = {
    val allSizes = listAll
    allSizes.collect {
      case size if size.getPizzaId == pizzaId => size
    }
  }

  @Transactional
  override def updatePizzaSize(pizzaSizeId: Integer, pizzaSize: PizzaSizeDto): PizzaSizeDto = {
    val currentPizzaSize = getPizzaSizeById(pizzaSizeId)
    val pizzaSizeOpt = pizzaSizeRepository.findByNameAndPizzaId(pizzaSize.getName, pizzaSize.getPizzaSizeId)
    pizzaSizeOpt match {
      case Some(size) =>
        if (size.getPizzaSizeId != pizzaSizeId) {
          throw new ExistingInstanceException(new Error("Pizza size already exists!"))
        }

      case None =>
    }

    pizzaSize.setPizzaSizeId(currentPizzaSize.getPizzaSizeId)
    val updatedPizzaSize = pizzaSizeRepository.saveAndFlush(tempConverter.pizzaSizeDtoToEntity(pizzaSize))
    val allCartItems = cartItemService.listAllByPizzaSizeId(pizzaSizeId)
    allCartItems.foreach(cartItem => {
      cartItem.setPrice(cartItem.getQuantity * updatedPizzaSize.getPrice)
      cartItemRepository.saveAndFlush(tempConverter.cartItemDtoToEntity(cartItem))
    })

    cartService.refreshAllCarts()
    tempConverter.pizzaSizeEntityToDto(updatedPizzaSize)
  }


  @Autowired
  private def injectAll(pizzaSizeRepository: PizzaSizeRepository, pizzaService: PizzaService, cartItemService: CartItemService,
                        cartService: CartService, tempConverter: TempConverter, cartItemRepository: CartItemRepository): Unit = {
    this.pizzaSizeRepository = pizzaSizeRepository
    this.pizzaService = pizzaService
    this.cartItemService = cartItemService
    this.cartService = cartService
    this.tempConverter = tempConverter
    this.cartItemRepository = cartItemRepository
  }

  @Transactional
  override def deleteAllByPizzaId(pizzaId: Integer): Unit = {
    val allPizzaSizes = listAllByPizzaId(pizzaId)
    allPizzaSizes.foreach(pizzaSize => {
      deletePizzaSize(pizzaSize.getPizzaSizeId)
    })
  }
}
