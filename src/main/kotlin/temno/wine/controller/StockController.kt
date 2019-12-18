package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import temno.wine.model.Stock
import temno.wine.payload.StockRepresentation
import temno.wine.repository.StockRepository


@RestController
@RequestMapping("/api/stock")
class StockController {

    @Autowired
    lateinit var stockRepository: StockRepository

    fun Stock.representation() = StockRepresentation(product.name, number)

    @GetMapping("/")
    @PreAuthorize("hasAuthority('MANAGER')")
    fun getAvailableProducts(): List<StockRepresentation> {
        print(stockRepository.findAll().map { it.representation() })
        return stockRepository.findAll().map { it.representation() }
    }
}