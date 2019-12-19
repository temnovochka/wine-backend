package temno.wine.payload

import temno.wine.model.OrderStatus

data class PurchaseRepresentation(val id: Long,
                                  val administratorId: Long?, val administratorLogin: String?,
                                  val managerId: Long, val managerLogin: String?,
                                  val status: OrderStatus,
                                  val supplier: String?,
                                  val isAddedIntoStock: Boolean,
                                  val products: Map<String, Int>)