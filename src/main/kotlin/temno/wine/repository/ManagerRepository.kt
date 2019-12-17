package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Manager
import temno.wine.model.User

interface ManagerRepository : JpaRepository<Manager, Long> {
    fun findByUser(user: User): Manager?
}
