package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import temno.wine.model.Product
import temno.wine.repository.ProductRepository

@RestController
@RequestMapping("/api/product")
class ProductController {
    @Autowired
    lateinit var productRepository: ProductRepository

    @GetMapping("/")
    fun getProducts(): List<Product> {
        return productRepository.findAll()
    }
}