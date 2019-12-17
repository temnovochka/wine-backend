package temno.wine.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import temno.wine.exception.ResourceNotFoundException
import temno.wine.model.Administrator
import temno.wine.model.Client
import temno.wine.model.Manager
import temno.wine.model.UserRole
import temno.wine.payload.UserProfile
import temno.wine.repository.AdministratorRepository
import temno.wine.repository.ClientRepository
import temno.wine.repository.ManagerRepository
import temno.wine.repository.UserRepository

@RestController
@RequestMapping("/api")
class UserController {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var clientRepository: ClientRepository
    @Autowired
    private lateinit var managerRepository: ManagerRepository
    @Autowired
    private lateinit var administratorRepository: AdministratorRepository


    @GetMapping("/user/{login}")
    fun getUser(@PathVariable login: String): UserProfile {
        val user = userRepository.findByLogin(login)
                .orElseThrow { ResourceNotFoundException("User", "username", login) }
        return UserProfile(user.id, user.login, user.email, user.role)
    }

    @GetMapping("/user")
    fun listUser(): List<UserProfile> {
        return userRepository.findAll()
                .map { user -> UserProfile(user.id, user.login, user.email, user.role) }
    }

    @Transactional
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PutMapping("/user/{login}")
    fun updateUser(@PathVariable login: String, @RequestBody updateInfo: UserProfile): UserProfile {
        val user = userRepository.findByLogin(login)
                .orElseThrow { ResourceNotFoundException("User", "username", login) }
        user.role = updateInfo.role
        val savedUser = userRepository.save(user)
        when (updateInfo.role) {
            UserRole.CLIENT -> {
                clientRepository.save(Client(user = savedUser))
            }
            UserRole.MANAGER -> {
                managerRepository.save(Manager(user = savedUser))
            }
            UserRole.ADMINISTRATOR -> {
                administratorRepository.save(Administrator(user = savedUser))
            }
        }
        return UserProfile(user.id, user.login, user.email, user.role)
    }
}
