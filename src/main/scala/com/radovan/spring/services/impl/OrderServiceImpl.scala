package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.{OrderDto, OrderItemDto}
import com.radovan.spring.entity.OrderItemEntity
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repositories.{OrderAddressRepository, OrderItemRepository, OrderRepository}
import com.radovan.spring.services.{CartItemService, CartService, CustomerService, OrderService, ShippingAddressService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp
import java.text.DecimalFormat
import java.time.{Instant, ZoneId}
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

@Service
class OrderServiceImpl extends OrderService {

  private var orderRepository: OrderRepository = _
  private var orderAddressRepository: OrderAddressRepository = _
  private var orderItemRepository: OrderItemRepository = _
  private var shippingAddressService: ShippingAddressService = _
  private var cartItemService: CartItemService = _
  private var customerService: CustomerService = _
  private var cartService: CartService = _
  private var tempConverter: TempConverter = _
  private val zoneId = ZoneId.of("UTC")
  private val decfor = new DecimalFormat("0.00")

  @Transactional
  override def addOrder(): OrderDto = {
    var returnValue = new OrderDto
    val customer = customerService.getCurrentCustomer
    val cart = cartService.getCartById(customer.getCartId)
    cartService.validateCart(cart.getCartId)
    val grandTotal = cartService.calculateGrandTotal(cart.getCartId)
    returnValue.setCartId(cart.getCartId)
    returnValue.setOrderPrice(grandTotal)
    val shippingAddress = shippingAddressService.getAddressById(customer.getShippingAddressId)
    val orderAddress = tempConverter.shippingAddressToOrderAddress(shippingAddress)
    val storedAddress = orderAddressRepository.save(tempConverter.orderAddressDtoToEntity(orderAddress))
    val orderEntity = tempConverter.orderDtoToEntity(returnValue)
    orderEntity.setAddress(storedAddress)
    val currentTime = Instant.now.atZone(zoneId)
    orderEntity.setCreateTime(Timestamp.valueOf(currentTime.toLocalDateTime))
    var storedOrder = orderRepository.save(orderEntity)
    val orderedItems = new ArrayBuffer[OrderItemDto]()
    val cartItems = cartItemService.listAllByCartId(cart.getCartId)
    cartItems.foreach(cartItem => orderedItems += tempConverter.cartItemToOrderItem(cartItem))

    val allOrderedItems = new ArrayBuffer[OrderItemEntity]()
    orderedItems.foreach(orderItem => {
      orderItem.setOrderId(storedOrder.getOrderId)
      val itemEntity = tempConverter.orderItemDtoToEntity(orderItem)
      val storedItem = orderItemRepository.save(itemEntity)
      allOrderedItems += storedItem
    })

    storedOrder.getOrderedItems.clear()
    storedOrder.getOrderedItems.addAll(allOrderedItems.asJava)
    storedOrder = orderRepository.saveAndFlush(storedOrder)
    returnValue = tempConverter.orderEntityToDto(storedOrder)
    cartItemService.eraseAllCartItems(cart.getCartId)
    cartService.refreshCartState(cart.getCartId)
    returnValue

  }

  @Transactional(readOnly = true)
  override def listAll: Array[OrderDto] = {
    val allOrders = orderRepository.findAll().asScala
    allOrders.map(orderEntity => tempConverter.orderEntityToDto(orderEntity)).toArray
  }

  @Transactional(readOnly = true)
  override def listAllByCartId(cartId: Integer): Array[OrderDto] = {
    val allOrders = listAll
    allOrders.collect {
      case order if order.getCartId == cartId => order
    }
  }

  @Transactional(readOnly = true)
  override def calculateOrderTotal(orderId: Integer): Float = {
    val orderTotalOption = orderItemRepository.calculateOrderTotal(orderId)
    orderTotalOption match {
      case Some(orderTotal) => decfor.format(orderTotal).toFloat
      case None => 0f
    }
  }

  @Transactional(readOnly = true)
  override def getOrderById(orderId: Integer): OrderDto = {
    val orderEntity = orderRepository.findById(orderId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The order has not been found!")))
    tempConverter.orderEntityToDto(orderEntity)
  }

  @Transactional
  override def deleteOrder(orderId: Integer): Unit = {
    getOrderById(orderId)
    orderRepository.deleteById(orderId)
    orderRepository.flush()
  }

  @Autowired
  private def injectAll(orderRepository: OrderRepository, orderItemRepository: OrderItemRepository, orderAddressRepository: OrderAddressRepository,
                        shippingAddressService: ShippingAddressService, cartItemService: CartItemService, cartService: CartService,
                        customerService: CustomerService, tempConverter: TempConverter): Unit = {

    this.orderRepository = orderRepository
    this.orderItemRepository = orderItemRepository
    this.orderAddressRepository = orderAddressRepository
    this.shippingAddressService = shippingAddressService
    this.cartItemService = cartItemService
    this.cartService = cartService
    this.customerService = customerService
    this.tempConverter = tempConverter
  }
}
