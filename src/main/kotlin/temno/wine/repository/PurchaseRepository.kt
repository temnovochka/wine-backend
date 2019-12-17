package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Purchase
import temno.wine.model.User

interface PurchaseRepository : JpaRepository<Purchase, Long> {
    fun findByManager_User(user: User): List<Purchase>
}