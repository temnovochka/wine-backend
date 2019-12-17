package temno.wine.payload

import temno.wine.model.UserRole

data class ClientProfile(val login: String, val id: Long,
                         val role: UserRole, val name: String,
                         val document: String, val birthday: String,
                         val email: String, val isConfirmed: Boolean)
