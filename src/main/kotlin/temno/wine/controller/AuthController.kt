package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import temno.wine.model.Client
import temno.wine.model.User
import temno.wine.model.UserRole
import temno.wine.payload.ApiResponse
import temno.wine.payload.JwtAuthenticationResponse
import temno.wine.payload.LoginRequest
import temno.wine.payload.RegisterRequest
import temno.wine.repository.ClientRepository
import temno.wine.repository.UserRepository
import temno.wine.security.JwtTokenProvider
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired
    internal lateinit var authenticationManager: AuthenticationManager

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var clientRepository: ClientRepository

    @Autowired
    internal lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    internal lateinit var tokenProvider: JwtTokenProvider

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail,
                        loginRequest.password
                )
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
        if (userRepository.existsByLogin(registerRequest.username)) return ResponseEntity(ApiResponse(false, "Username is already taken!"),
                HttpStatus.BAD_REQUEST)

        if (userRepository.existsByEmail(registerRequest.email)) return ResponseEntity(ApiResponse(false, "Email Address already in use!"),
                HttpStatus.BAD_REQUEST)

        val role = UserRole.CLIENT
        val password = passwordEncoder.encode(registerRequest.password)

        val user = User(registerRequest.username, registerRequest.email, password, role)
        val result = userRepository.save(user)

        val client = Client(user = result)
        clientRepository.save(client)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/user/{login}")
                .buildAndExpand(result.login)
                .toUri()
        return ResponseEntity
                .created(location)
                .body(ApiResponse(true, "User registered successfully"))
    }
}
