package com.radovan.spring.controllers

import com.radovan.spring.dto.UserDto
import com.radovan.spring.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(value = Array("/api/users"))
class UserController {

  @Autowired
  private var userService:UserService = _

  @GetMapping(value=Array("/currentUser"))
  def getCurrentUser: ResponseEntity[UserDto] = {
    new ResponseEntity(userService.getCurrentUser, HttpStatus.OK)
  }
}
