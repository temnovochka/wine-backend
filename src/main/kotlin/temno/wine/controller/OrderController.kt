package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import temno.wine.exception.ResourceNotFoundException
import temno.wine.model.*
import temno.wine.payload.ApiResponse
import temno.wine.payload.OrderPayload
import temno.wine.payload.OrderRepresentation
import temno.wine.repository.*

@RestController
@RequestMapping("/api/order")
class OrderController {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var clientRepository: ClientRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var managerRepository: ManagerRepository

    @Autowired
    lateinit var listOfProductItemsRepository: ListOfProductItemsRepository

    @Autowired
    lateinit var stockRepository: StockRepository

    fun Order.representation() = OrderRepresentation(id, client.id, client.user.login,
            manager?.id, manager?.user?.login, status, paymentStatus)

    @PostMapping("/")
    @PreAuthorize("hasAuthority('CLIENT')")
    fun create(@RequestBody orderPayload: OrderPayload, @AuthenticationPrincipal user: User): ResponseEntity<*> {
        val client = clientRepository.findByUserLogin(user.login)
                .orElseThrow { ResourceNotFoundException("User", "username", user.login) }
        var order = Order(client, null, OrderStatus.NEW, PaymentStatus.NOT_PAID)
        order = orderRepository.save(order)
        val productItems = orderPayload.products.map {
            val product = productRepository.findById(it.key)
                    .orElseThrow { ResourceNotFoundException("Product", "id", it.key) }
            ListOfProductItems(product, order, null, it.value)
        }
        listOfProductItemsRepository.saveAll(productItems)
        return ResponseEntity.ok(order.representation())
    }

    @GetMapping("/")
    fun list(@AuthenticationPrincipal user: User,
             @RequestParam(name = "manager_id", required = false) managerId: Long?): ResponseEntity<*> {
        return when (user.role) {
            UserRole.CLIENT -> ResponseEntity.ok(orderRepository.findByClient_User(user).map { it.representation() })
            UserRole.MANAGER -> {
                if (managerId != null) {
                    ResponseEntity.ok(orderRepository.findByManager_Id(managerId).map { it.representation() })
                } else {
                    ResponseEntity.ok(orderRepository.findAll().map { it.representation() })
                }
            }
            else -> ResponseEntity(ApiResponse(false, "Is not possible for your role"), HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/change/{id}")
    fun change(@AuthenticationPrincipal user: User,
               @PathVariable id: Long,
               @RequestBody orderUpdate: OrderRepresentation): ResponseEntity<*> {
        val currentOrder = orderRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Order", "id", id) }
        when {
            user.role == UserRole.CLIENT -> {
                if (currentOrder.client.user.id != user.id) {
                    return ResponseEntity(ApiResponse(false, "Is not possible for you"), HttpStatus.BAD_REQUEST)
                }
                if (currentOrder.paymentStatus == PaymentStatus.NOT_PAID) {
                    currentOrder.paymentStatus = orderUpdate.paymentStatus
                }
                orderRepository.save(currentOrder)
                return ResponseEntity.ok(currentOrder.representation())
            }
            user.role == UserRole.MANAGER -> {
                if (currentOrder.manager?.id != orderUpdate.managerId && orderUpdate.managerId != null) {
                    val manager = managerRepository.findById(orderUpdate.managerId)
                            .orElseThrow { ResourceNotFoundException("Manager", "id", orderUpdate.managerId) }
                    currentOrder.manager = manager
                }
                when (currentOrder.status to orderUpdate.status) {
                    OrderStatus.NEW to OrderStatus.IN_PROGRESS -> {
                        if (orderUpdate.managerId != null) {
                            currentOrder.status = orderUpdate.status
                        } else {
                            return ResponseEntity(ApiResponse(false, "Required manager"), HttpStatus.BAD_REQUEST)
                        }
                    }
                    OrderStatus.IN_PROGRESS to OrderStatus.DONE -> currentOrder.status = orderUpdate.status
                    OrderStatus.IN_PROGRESS to OrderStatus.NOT_DONE -> currentOrder.status = orderUpdate.status
                    else -> return ResponseEntity(ApiResponse(false, "It is not possible"), HttpStatus.BAD_REQUEST)
                }
                orderRepository.save(currentOrder)
                return ResponseEntity.ok(currentOrder.representation())
            }
            else -> return ResponseEntity(ApiResponse(false, "Is not possible for your role"), HttpStatus.BAD_REQUEST)
        }
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    fun check(@AuthenticationPrincipal user: User,
              @PathVariable id: Long) {
        val manager = managerRepository.findByUser(user)
                ?: throw ResourceNotFoundException("Manager", "username", user.login)
        val orders = listOf(orderRepository.findById(id).orElseThrow { ResourceNotFoundException("Order", "order_id", id) })
        // if (id == null) orderRepository.findAllByStatus(OrderStatus.IN_PROGRESS) else
        for (order in orders) {
            val prodInOrder = listOfProductItemsRepository.findByOrder_Id(order.id).map { it.product to it.number }
            val stock = stockRepository.findAll()
            val stockProductToNumber = stock.map { it.product to it.number }.toMap(mutableMapOf())
            val stockAll = stock.map { it.product to it }.toMap()

            val newNumberPerProduct = mutableMapOf<Stock, Int>()
            var isNotEnough = false

            for ((prod, num) in prodInOrder) {
                if (num <= stockProductToNumber.getOrDefault(prod, 0)) {
                    val st = stockAll.getValue(prod)
                    newNumberPerProduct[st] = st.number - num
                } else {
                    isNotEnough = true
                    break
                }
            }

            when (isNotEnough) {
                true -> order.status = OrderStatus.NOT_DONE
                else -> {
                    order.status = OrderStatus.DONE

                    for ((oneStock, num) in newNumberPerProduct) {
                        oneStock.number = num
                        oneStock.manager = manager
                    }
                    stockRepository.saveAll(newNumberPerProduct.keys)
                }
            }
        }
        orderRepository.saveAll(orders)
    }
}