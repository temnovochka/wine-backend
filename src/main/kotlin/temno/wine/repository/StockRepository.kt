package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Stock

interface StockRepository : JpaRepository<Stock, Long>