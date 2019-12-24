package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Order
import temno.wine.model.OrderStatus
import temno.wine.model.User

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByClient_User(user: User): List<Order>
    fun findByManager_Id(managerId: Long): List<Order>
}