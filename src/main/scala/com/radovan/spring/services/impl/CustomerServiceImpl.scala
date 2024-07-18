package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.CustomerDto
import com.radovan.spring.entity.{CartEntity, RoleEntity, UserEntity}
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repositories.{CartRepository, CustomerRepository, RoleRepository, ShippingAddressRepository, UserRepository}
import com.radovan.spring.services.{CustomerService, OrderService, UserService}
import com.radovan.spring.utils.RegistrationForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

@Service
class CustomerServiceImpl extends CustomerService {

  private var customerRepository:CustomerRepository = _
  private var userRepository:UserRepository = _
  private var roleRepository:RoleRepository = _
  private var cartRepository:CartRepository = _
  private var shippingAddressRepository:ShippingAddressRepository = _
  private var tempConverter:TempConverter = _
  private var passwordEncoder:BCryptPasswordEncoder = _
  private var userService:UserService = _
  private var orderService:OrderService = _

  @Transactional
  override def addCustomer(form: RegistrationForm): CustomerDto = {
    val userOption: Option[UserEntity] = userRepository.findByEmail(form.getUser.getEmail)
    userOption match {
      case Some(_) => throw new ExistingInstanceException(new Error("This email exists already!"))
      case None =>
    }

    var returnValue = form.getCustomer
    val user = form.getUser
    user.setEnabled(1.asInstanceOf[Short])
    user.setPassword(passwordEncoder.encode(user.getPassword))
    var roleEntity = roleRepository.findByRole("ROLE_USER")
      .getOrElse(roleRepository.save(new RoleEntity("ROLE_USER")))
    val rolesIds = new ArrayBuffer[Integer]()
    rolesIds += roleEntity.getId
    user.setRolesIds(rolesIds.toArray)
    val userEntity = tempConverter.userDtoToEntity(user)
    val storedUser = userRepository.save(userEntity)
    var users = roleEntity.getUsers.asScala
    if(users == null){
      users = new ArrayBuffer[UserEntity]()
    }

    users += storedUser
    roleEntity.setUsers(users.asJava)
    roleEntity = roleRepository.saveAndFlush(roleEntity)

    val shippingAddress = form.getShippingAddress
    val storedAddress = shippingAddressRepository.save(tempConverter.shippingAddressDtoToEntity(shippingAddress))

    val cartEntity = new CartEntity
    cartEntity.setCartPrice(0f)
    val storedCart = cartRepository.saveAndFlush(cartEntity)

    returnValue.setUserId(storedUser.getId)
    returnValue.setCartId(storedCart.getCartId)
    returnValue.setShippingAddressId(storedAddress.getShippingAddressId)
    val storedCustomer = customerRepository.save(tempConverter.customerDtoToEntity(returnValue))

    storedCart.setCustomer(storedCustomer)
    cartRepository.saveAndFlush(storedCart)

    storedAddress.setCustomer(storedCustomer)
    shippingAddressRepository.saveAndFlush(storedAddress)

    returnValue = tempConverter.customerEntityToDto(storedCustomer)
    returnValue
  }

  @Transactional(readOnly = true)
  override def getCustomerById(customerId: Integer): CustomerDto = {
    val customerEntity = customerRepository.findById(customerId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The customer has not been found!")))
    tempConverter.customerEntityToDto(customerEntity)
  }

  @Transactional(readOnly = true)
  override def getCustomerByUserId(userId: Integer): CustomerDto = {
    val customerOption = customerRepository.findByUserId(userId)
    customerOption match {
      case Some(customer) => tempConverter.customerEntityToDto(customer)
      case None => throw new InstanceUndefinedException(new Error("The customer has not been found!"))
    }
  }

  @Transactional(readOnly = true)
  override def listAll: Array[CustomerDto] = {
    val allCustomers = customerRepository.findAll().asScala
    allCustomers.map(customer => tempConverter.customerEntityToDto(customer)).toArray
  }

  @Transactional(readOnly = true)
  override def getCurrentCustomer: CustomerDto = {
    val authUser = userService.getCurrentUser
    getCustomerByUserId(authUser.getId)
  }

  @Transactional
  override def updateCustomer(customer:CustomerDto): CustomerDto = {
    val currentCustomer = getCurrentCustomer
    currentCustomer.setCustomerPhone(customer.getCustomerPhone)
    val updatedCustomer = customerRepository.saveAndFlush(tempConverter.customerDtoToEntity(currentCustomer))
    tempConverter.customerEntityToDto(updatedCustomer)
  }

  @Transactional
  override def removeCustomer(customerId: Integer): Unit = {
    val customer = getCustomerById(customerId)
    val orders = orderService.listAllByCartId(customer.getCartId)
    orders.foreach(order => orderService.deleteOrder(order.getOrderId))
    customerRepository.deleteById(customerId)
    customerRepository.flush()
  }

  @Autowired
  private def injectAll(customerRepository: CustomerRepository,userRepository: UserRepository,roleRepository: RoleRepository,
                        cartRepository: CartRepository,shippingAddressRepository: ShippingAddressRepository,tempConverter: TempConverter,
                        passwordEncoder: BCryptPasswordEncoder,userService: UserService,orderService: OrderService):Unit = {

    this.customerRepository = customerRepository
    this.userRepository = userRepository
    this.roleRepository = roleRepository
    this.cartRepository = cartRepository
    this.shippingAddressRepository = shippingAddressRepository
    this.tempConverter = tempConverter
    this.passwordEncoder = passwordEncoder
    this.userService = userService
    this.orderService = orderService
  }
}
