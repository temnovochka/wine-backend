package temno.wine.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


data class RegisterRequest(
        @NotBlank
        var username: String,

        @NotBlank
        @Email
        var email: String,

        @NotBlank
        var password: String
)
