package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.Client
import java.util.*

interface ClientRepository : JpaRepository<Client, Long> {
    fun findByUserLoginAndDeleted(login: String, deleted: Boolean): Client?
    fun findByDeleted(deleted: Boolean): Optional<List<Client>>
}
