package com.radovan.spring.config

import com.radovan.spring.interceptors.AuthInterceptor
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, WebMvcConfigurer}

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = Array("com.radovan.spring"))
class SpringMvcConfiguration extends WebMvcConfigurer {

  @Autowired
  private var authInterceptor: AuthInterceptor = _

  override def addInterceptors(registry: InterceptorRegistry): Unit = {
    registry.addInterceptor(authInterceptor)
  }

  @Bean
  def getMapper: ModelMapper = {
    val returnValue = new ModelMapper
    returnValue.getConfiguration.setAmbiguityIgnored(true).setFieldAccessLevel(AccessLevel.PRIVATE)
    returnValue.getConfiguration.setMatchingStrategy(MatchingStrategies.STRICT)
    returnValue
  }
}
