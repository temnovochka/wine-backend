package temno.wine.payload

import temno.wine.model.OrderStatus
import temno.wine.model.PaymentStatus


data class OrderRepresentation(val id: Long,
                               val clientId: Long, val clientLogin: String,
                               val managerId: Long?, val managerLogin: String?,
                               val status: OrderStatus, val paymentStatus: PaymentStatus,
                               val products: Map<String, Int>,
                               val price: Double,
                               val clientCard: String)