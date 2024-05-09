package com.radovan.spring.services

import com.radovan.spring.dto.UserDto
import org.springframework.security.core.Authentication

trait UserService {

  def getUserById(userId:Integer):UserDto
  def listAll:Array[UserDto]
  def getUserByEmail(email:String):UserDto
  def getCurrentUser:UserDto
  def suspendUser(userId:Integer):Unit
  def reactivateUser(userId:Integer):Unit
  def isAdmin:Boolean
  def authenticateUser(username:String, password:String):Option[Authentication]
}
