package com.radovan.spring.security

import com.radovan.spring.services.impl.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.{AuthorizeHttpRequestsConfigurer, CorsConfigurer, CsrfConfigurer, ExceptionHandlingConfigurer, SessionManagementConfigurer}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration {

  private var jwtRequestFilter: JwtRequestFilter = _
  private var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint = _
  private var corsHandler: CorsHandler = _

  @Autowired
  private def injectAll(jwtRequestFilter: JwtRequestFilter, jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
                        corsHandler: CorsHandler): Unit = {
    this.jwtRequestFilter = jwtRequestFilter
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint
    this.corsHandler = corsHandler
  }

  @Bean
  @throws[Exception]
  def securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain = {
    httpSecurity.csrf((csrf: CsrfConfigurer[HttpSecurity]) => csrf.disable).cors((cors: CorsConfigurer[HttpSecurity]) => cors.configurationSource(corsHandler))
      .sessionManagement((session: SessionManagementConfigurer[HttpSecurity]) => session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling((exception: ExceptionHandlingConfigurer[HttpSecurity]) => exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
      .authorizeHttpRequests((authorize: AuthorizeHttpRequestsConfigurer[HttpSecurity]#AuthorizationManagerRequestMatcherRegistry) => authorize
        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
        .requestMatchers("/login", "/register").anonymous()
        .requestMatchers("/api/cart/**", "/api/order/**").hasAuthority("ROLE_USER")
        .anyRequest.authenticated)
      .addFilterBefore(jwtRequestFilter, classOf[UsernamePasswordAuthenticationFilter]).build
  }

  @Bean
  def authenticationManager: AuthenticationManager = {
    val authProvider = new DaoAuthenticationProvider
    authProvider.setUserDetailsService(userDetailsService)
    authProvider.setPasswordEncoder(passwordEncoder)
    new ProviderManager(authProvider)
  }

  @Bean
  def userDetailsService = new UserDetailsImpl

  @Bean
  def passwordEncoder = new BCryptPasswordEncoder

}