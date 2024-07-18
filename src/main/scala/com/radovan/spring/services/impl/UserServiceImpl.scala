package com.radovan.spring.services.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.UserDto
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repositories.{RoleRepository, UserRepository}
import com.radovan.spring.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.{Authentication, AuthenticationException}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Service
class UserServiceImpl extends UserService {

  private var userRepository:UserRepository = _
  private var roleRepository:RoleRepository = _
  private var tempConverter:TempConverter = _
  private var authenticationManager:AuthenticationManager = _

  @Transactional(readOnly = true)
  override def getUserById(userId: Integer): UserDto = {
    val userEntity = userRepository.findById(userId)
      .orElseThrow(() => new InstanceUndefinedException(new Error("The user has not been found!")))
    tempConverter.userEntityToDto(userEntity)
  }

  @Transactional(readOnly = true)
  override def listAll: Array[UserDto] = {
    val allUsers = userRepository.findAll().asScala
    allUsers.map(userEntity => tempConverter.userEntityToDto(userEntity)).toArray
  }

  @Transactional(readOnly = true)
  override def getUserByEmail(email: String): UserDto = {
    val userOption = userRepository.findByEmail(email)
    userOption match {
      case Some(user) => tempConverter.userEntityToDto(user)
      case None => throw new InstanceUndefinedException(new Error("The user has not been found!"))
    }
  }

  @Transactional(readOnly = true)
  override def getCurrentUser: UserDto = {
    val authentication = SecurityContextHolder.getContext.getAuthentication
    if (authentication.isAuthenticated) {
      val currentUsername = authentication.getName
      userRepository.findByEmail(currentUsername)
        .map(tempConverter.userEntityToDto)
        .getOrElse(throw new InstanceUndefinedException(new Error("Invalid user!")))
    } else {
      throw new InstanceUndefinedException(new Error("Invalid user!"))
    }
  }

  @Transactional
  override def suspendUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(0.asInstanceOf[Short])
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }

  @Transactional
  override def reactivateUser(userId: Integer): Unit = {
    val user = getUserById(userId)
    user.setEnabled(1.asInstanceOf[Short])
    userRepository.saveAndFlush(tempConverter.userDtoToEntity(user))
  }

  @Transactional(readOnly = true)
  override def isAdmin: Boolean = {
    val authUser = getCurrentUser
    roleRepository.findByRole("ADMIN") match {
      case Some(role) => authUser.getRolesIds.contains(role.getId)
      case None => false
    }
  }

  @Transactional(readOnly = true)
  override def authenticateUser(username: String, password: String): Option[Authentication] = {
    val authReq = new UsernamePasswordAuthenticationToken(username, password)
    val userOptional = userRepository.findByEmail(username)
    userOptional.flatMap { user =>
      try {
        val auth = authenticationManager.authenticate(authReq)
        Some(auth)
      } catch {
        case _: AuthenticationException => None
      }
    }
  }

  @Autowired
  private def injectAll(userRepository: UserRepository,roleRepository: RoleRepository,tempConverter: TempConverter,
                        authenticationManager: AuthenticationManager):Unit = {
    this.userRepository = userRepository
    this.roleRepository = roleRepository
    this.tempConverter = tempConverter
    this.authenticationManager = authenticationManager
  }
}
