package com.radovan.spring.repositories

import com.radovan.spring.entity.PizzaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait PizzaRepository extends JpaRepository[PizzaEntity, Integer]{

}
