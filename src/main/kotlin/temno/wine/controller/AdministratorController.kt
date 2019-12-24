package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import temno.wine.exception.ResourceNotFoundException
import temno.wine.model.Administrator
import temno.wine.model.User
import temno.wine.payload.AdministratorRepresentation
import temno.wine.payload.ApiResponse
import temno.wine.repository.AdministratorRepository

@RestController
@RequestMapping("/api/administrator")
class AdministratorController {
    @Autowired
    lateinit var administratorRepository: AdministratorRepository

    fun Administrator.representation() = AdministratorRepresentation(id, user.login, name)

    @GetMapping("/{login}")
    fun getProfile(@PathVariable login: String, @AuthenticationPrincipal currentUser: User): ResponseEntity<*> {
        val admin = administratorRepository.findByUserLoginAndDeleted(login, false)
                ?: throw ResourceNotFoundException("User", "username", login)
        if (admin.user.id != currentUser.id) {
            return ResponseEntity(ApiResponse(false, "No permission"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity.ok(admin.representation())
    }
}