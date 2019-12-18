package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.ListOfProductItems

interface ListOfProductItemsRepository : JpaRepository<ListOfProductItems, Long> {
    fun findByOrder_Id(order_id: Long): List<ListOfProductItems>
    fun findByPurchase_Id(purchase_id: Long): List<ListOfProductItems>
}