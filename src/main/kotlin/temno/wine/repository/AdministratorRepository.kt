package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Administrator
import temno.wine.model.User

interface AdministratorRepository : JpaRepository<Administrator, Long> {
    fun findByUserLogin(login: String): Administrator?
    fun findByUserLoginAndDeleted(login: String, deleted: Boolean): Administrator?
    fun findByUserAndDeleted(user: User, deleted: Boolean): Administrator?
}
