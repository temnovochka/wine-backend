package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.ListOfProductItems
import temno.wine.model.Order

interface ListOfProductItemsRepository : JpaRepository<ListOfProductItems, Long> {
    fun findByOrder(order: Order): List<ListOfProductItems>
}