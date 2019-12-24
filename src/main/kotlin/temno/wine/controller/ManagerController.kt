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
import temno.wine.model.Manager
import temno.wine.model.User
import temno.wine.payload.ApiResponse
import temno.wine.payload.ManagerRepresentation
import temno.wine.repository.ManagerRepository

@RestController
@RequestMapping("/api/manager")
class ManagerController {
    @Autowired
    lateinit var managerRepository: ManagerRepository

    fun Manager.representation() = ManagerRepresentation(id, user.login, name)

    @GetMapping("/{login}")
    fun getProfile(@PathVariable login: String, @AuthenticationPrincipal currentUser: User): ResponseEntity<*> {
        val manager = managerRepository.findByUserLoginAndDeleted(login, false)
                ?: throw ResourceNotFoundException("User", "username", login)
        if (manager.user.id != currentUser.id) {
            return ResponseEntity(ApiResponse(false, "No permission"), HttpStatus.UNAUTHORIZED)
        }
        return ResponseEntity.ok(manager.representation())
    }
}