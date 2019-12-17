package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Product

interface ProductRepository : JpaRepository<Product, Long>