package com.radovan.spring.services

import com.radovan.spring.dto.ShippingAddressDto

trait ShippingAddressService {

  def updateShippingAddress(id:Integer, address:ShippingAddressDto):ShippingAddressDto
  def getAddressById(addressId:Integer):ShippingAddressDto
}
