package temno.wine.repository

import org.springframework.data.jpa.repository.JpaRepository
import temno.wine.model.User
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByLoginOrEmail(login: String, email: String): Optional<User>
    fun findByLogin(login: String): Optional<User>
    fun existsByLogin(login: String): Boolean
    fun existsByEmail(email: String): Boolean
}