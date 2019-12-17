package temno.wine.model

import org.hibernate.annotations.NaturalId
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

enum class UserRole {
    SYSTEM_ADMIN, CLIENT, MANAGER, ADMINISTRATOR
}

@Entity
@Table(name = "_user")
data class User(
        @NotBlank
        var login: String,

        @NaturalId
        @NotBlank
        @Email var email: String,

        @NotBlank
        var password: String,

        @Enumerated(EnumType.STRING)
        var role: UserRole
) : ModelWithTimestamp(), Authentication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @Transient
    private var isAuthenticated = false

    override fun getName() = login
    override fun getAuthorities() = listOf(SimpleGrantedAuthority(role.name))

    override fun isAuthenticated() = isAuthenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }

    override fun getCredentials() = this
    override fun getPrincipal() = this
    override fun getDetails() = this


}
