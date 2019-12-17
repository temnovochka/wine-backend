package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Client
import java.util.*

interface ClientRepository : JpaRepository<Client, Long> {
    fun findByUserLogin(login: String): Optional<Client>
}
