package temno.wine.payload

data class JwtAuthenticationResponse(val accessToken: String) {
    val tokenType = "Bearer"
}
