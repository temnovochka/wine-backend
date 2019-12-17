package temno.wine.payload

import temno.wine.model.UserRole

data class UserProfile(
        val id: Long,
        val login: String,
        val email: String,
        val role: UserRole
)