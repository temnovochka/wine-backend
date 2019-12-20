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
import temno.wine.payload.PurchasePayload
import temno.wine.payload.PurchaseRepresentation
import temno.wine.repository.*

@RestController
@RequestMapping("/api/purchase")
class PurchaseController {
    @Autowired
    lateinit var purchaseRepository: PurchaseRepository

    @Autowired
    lateinit var managerRepository: ManagerRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var listOfProductItemsRepository: ListOfProductItemsRepository

    @Autowired
    lateinit var stockRepository: StockRepository

    @Autowired
    lateinit var administratorRepository: AdministratorRepository

    fun Purchase.representation() = PurchaseRepresentation(id, administrator?.id, administrator?.user?.login,
            manager.id, manager.user.login, status, supplier, isAddedIntoStock,
            listOfProductItemsRepository.findByPurchase_Id(id).map { it.product.name to it.number }.toMap())

    @GetMapping("/")
    fun list(@AuthenticationPrincipal user: User): ResponseEntity<*> {
        return when (user.role) {
            UserRole.MANAGER -> ResponseEntity.ok(purchaseRepository.findByManager_User(user).map { it.representation() })
            UserRole.ADMINISTRATOR -> ResponseEntity.ok(purchaseRepository.findAll().map { it.representation() })
            else -> ResponseEntity(ApiResponse(false, "Is not possible for your role"), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('MANAGER')")
    fun create(@RequestBody purchasePayload: PurchasePayload,
               @AuthenticationPrincipal user: User): ResponseEntity<*> {
        val manager = managerRepository.findByUser(user)
                ?: throw ResourceNotFoundException("Manager", "username", user.login)
        var purchase = Purchase(manager, null, null, OrderStatus.NEW, false)
        purchase = purchaseRepository.save(purchase)
        val productItems = purchasePayload.products.map {
            val product = productRepository.findById(it.key)
                    .orElseThrow { ResourceNotFoundException("Product", "id", it.key) }
            ListOfProductItems(product, null, purchase, it.value)
        }
        listOfProductItemsRepository.saveAll(productItems)
        return ResponseEntity.ok(purchase.representation())
    }

    @PutMapping("/{id}")
    fun change(@AuthenticationPrincipal user: User,
               @PathVariable id: Long,
               @RequestBody purchaseUpdate: PurchaseRepresentation): ResponseEntity<*> {
        val currentPurchase = purchaseRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Purchase", "id", id) }
        when {
            user.role == UserRole.MANAGER -> {
                if (currentPurchase.manager.user.id != user.id) {
                    return ResponseEntity(ApiResponse(false, "Is not possible for you"), HttpStatus.BAD_REQUEST)
                }
                if (currentPurchase.status == OrderStatus.DONE && !currentPurchase.isAddedIntoStock) {
                    when (purchaseUpdate.status to purchaseUpdate.isAddedIntoStock) {
                        OrderStatus.CLOSED to true -> {
                            currentPurchase.status = purchaseUpdate.status
                            currentPurchase.isAddedIntoStock = purchaseUpdate.isAddedIntoStock
                            val manager = managerRepository.findByUser(user)
                                    ?: throw ResourceNotFoundException("Manager", "username", user.login)

                            val stockDataForSave = stockRepository.findAll().map { it.product to it }.toMap()
                            val products = listOfProductItemsRepository.findByPurchase_Id(currentPurchase.id)
                            for (prod in products) {
                                when (val currentStock = stockDataForSave[prod.product]) {
                                    null -> stockRepository.save(Stock(prod.product, prod.number, manager))
                                    else -> {
                                        currentStock.number = prod.number + currentStock.number
                                        currentStock.manager = manager
                                        stockRepository.save(currentStock)
                                    }
                                }
                            }
                            currentPurchase.isAddedIntoStock = true
                        }
                        else -> return ResponseEntity(ApiResponse(false, "Is not possible to make such changes"),
                                HttpStatus.BAD_REQUEST)
                    }
                }
                purchaseRepository.save(currentPurchase)
                return ResponseEntity.ok(currentPurchase.representation())
            }
            user.role == UserRole.ADMINISTRATOR -> {
                currentPurchase.supplier = purchaseUpdate.supplier
                currentPurchase.administrator = administratorRepository.findByUser(user)
                        ?: throw ResourceNotFoundException("Administrator", "username", user.login)
                when (currentPurchase.status to purchaseUpdate.status) {
                    OrderStatus.NEW to OrderStatus.IN_PROGRESS -> currentPurchase.status = purchaseUpdate.status
                    OrderStatus.IN_PROGRESS to OrderStatus.IN_PROGRESS -> currentPurchase.status = purchaseUpdate.status
                    OrderStatus.IN_PROGRESS to OrderStatus.DONE -> currentPurchase.status = purchaseUpdate.status
                    OrderStatus.IN_PROGRESS to OrderStatus.NOT_DONE -> currentPurchase.status = purchaseUpdate.status
                    OrderStatus.NOT_DONE to OrderStatus.IN_PROGRESS -> currentPurchase.status = purchaseUpdate.status
                    else -> return ResponseEntity(ApiResponse(false, "Is not possible to make such changes"),
                            HttpStatus.BAD_REQUEST)
                }
                purchaseRepository.save(currentPurchase)
                return ResponseEntity.ok(currentPurchase)
            }
            else -> return ResponseEntity(ApiResponse(false, "Is not possible for your role"), HttpStatus.BAD_REQUEST)
        }
    }
}