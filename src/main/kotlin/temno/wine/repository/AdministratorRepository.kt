package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Administrator

interface AdministratorRepository: JpaRepository<Administrator, Long>
