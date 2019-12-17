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

    fun Purchase.representation() = PurchaseRepresentation(id, administrator?.id, administrator?.user?.login,
            manager.id, manager.user.login, status, supplier, isAddedIntoStock)

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
}