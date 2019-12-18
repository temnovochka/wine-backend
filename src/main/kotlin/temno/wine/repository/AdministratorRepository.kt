package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Administrator
import temno.wine.model.User

interface AdministratorRepository: JpaRepository<Administrator, Long> {
    fun findByUser(user: User) : Administrator?
}
