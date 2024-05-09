package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderItemDto
import com.radovan.spring.repositories.OrderItemRepository
import com.radovan.spring.services.OrderItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
class OrderItemServiceImpl extends OrderItemService{

  private var orderItemRepository:OrderItemRepository = _
  private var tempConverter:TempConverter = _

  @Transactional(readOnly = true)
  override def listAllByOrderId(orderId: Integer): Array[OrderItemDto] = {
    val allItems = orderItemRepository.findAll().asScala
    val allItemsDto = allItems.map(itemEntity => tempConverter.orderItemEntityToDto(itemEntity)).toArray
    allItemsDto.collect {
      case item if item.getOrderId == orderId => item
    }

  }

  @Autowired
  private def injectAll(orderItemRepository: OrderItemRepository,tempConverter: TempConverter):Unit = {
    this.orderItemRepository = orderItemRepository
    this.tempConverter = tempConverter
  }
}
