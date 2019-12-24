package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Manager
import temno.wine.model.User
import java.util.*

interface ManagerRepository : JpaRepository<Manager, Long> {
    fun findByUserLogin(login: String): Manager?
    fun findByUserLoginAndDeleted(login: String, deleted: Boolean): Manager?
    fun findByUserAndDeleted(user: User, deleted: Boolean): Manager?
    fun findByIdAndDeleted(id: Long, deleted: Boolean): Optional<Manager>
}
