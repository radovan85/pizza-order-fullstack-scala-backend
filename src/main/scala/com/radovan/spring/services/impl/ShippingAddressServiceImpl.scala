package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.exceptions.{InstanceUndefinedException, OperationNotAllowedException}
import com.radovan.spring.repositories.ShippingAddressRepository
import com.radovan.spring.services.{CustomerService, ShippingAddressService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShippingAddressServiceImpl extends ShippingAddressService {

  private var addressRepository:ShippingAddressRepository = _
  private var tempConverter:TempConverter = _
  private var customerService:CustomerService = _

  @Transactional
  override def updateShippingAddress(id: Integer, address: ShippingAddressDto): ShippingAddressDto = {
    val customer = customerService.getCurrentCustomer
    if(id != customer.getShippingAddressId){
      throw new OperationNotAllowedException(new Error("This operation is not allowed!"))
    }

    val currentAddress = getAddressById(customer.getShippingAddressId)
    address.setShippingAddressId(currentAddress.getShippingAddressId)
    address.setCustomerId(currentAddress.getCustomerId)
    val updatedAddress = addressRepository.saveAndFlush(tempConverter.shippingAddressDtoToEntity(address))
    tempConverter.shippingAddressEntityToDto(updatedAddress)
  }

  @Transactional(readOnly = true)
  override def getAddressById(addressId: Integer): ShippingAddressDto = {
    val addressEntity = addressRepository.findById(addressId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The address has not been found!")))
    tempConverter.shippingAddressEntityToDto(addressEntity)
  }

  @Autowired
  private def injectAll(addressRepository: ShippingAddressRepository,tempConverter: TempConverter,customerService: CustomerService):Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
    this.customerService = customerService
  }
}
