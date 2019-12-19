package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import temno.wine.exception.ResourceNotFoundException
import temno.wine.model.Client
import temno.wine.model.User
import temno.wine.model.UserRole
import temno.wine.payload.ApiResponse
import temno.wine.payload.ClientProfile
import temno.wine.repository.ClientRepository


@RestController
@RequestMapping("/api/client")
class ClientController {
    @Autowired
    private lateinit var clientRepository: ClientRepository

    fun Client.representation() = ClientProfile(
            user.login,
            user.id,
            user.role,
            name,
            document,
            card,
            birthday,
            user.email,
            isConfirmed
    )

    @GetMapping("/{login}")
    fun getProfile(@PathVariable login: String, @AuthenticationPrincipal currentUser: User): ResponseEntity<*> {
        val client = clientRepository.findByUserLogin(login)
                .orElseThrow { ResourceNotFoundException("User", "username", login) }
        if (client.user.id != currentUser.id) {
            return ResponseEntity(ApiResponse(false, "No permission"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity.ok(client.representation())
    }

    @PutMapping("/{login}")
    fun updateProfile(
            @PathVariable login: String,
            @RequestBody updatedProfile: ClientProfile,
            @AuthenticationPrincipal currentUser: User): ResponseEntity<*> {
        val client = clientRepository.findByUserLogin(login)
                .orElseThrow { ResourceNotFoundException("User", "username", login) }
        when {
            client.user.id == currentUser.id -> {
                client.name = updatedProfile.name
                client.document = updatedProfile.document
                client.birthday = updatedProfile.birthday
                client.card = updatedProfile.card
            }
            currentUser.role == UserRole.MANAGER -> {
                client.isConfirmed = updatedProfile.isConfirmed
            }
            else -> return ResponseEntity(ApiResponse(false, "No permission"), HttpStatus.BAD_REQUEST)
        }
        val newClient = clientRepository.save(client)
        return ResponseEntity.ok(newClient.representation())
    }


    @GetMapping("/")
    @PreAuthorize("hasAuthority('MANAGER')")
    fun list(): List<ClientProfile> {
        return clientRepository.findAll().map { it.representation() }
    }
}
