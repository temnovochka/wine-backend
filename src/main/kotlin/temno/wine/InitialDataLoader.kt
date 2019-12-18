package temno.wine

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import temno.wine.model.*
import temno.wine.repository.AdministratorRepository
import temno.wine.repository.ClientRepository
import temno.wine.repository.ManagerRepository
import temno.wine.repository.UserRepository
import javax.transaction.Transactional

@Component
class InitialDataLoader : ApplicationRunner {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var clientRepository: ClientRepository
    @Autowired
    private lateinit var managerRepository: ManagerRepository
    @Autowired
    private lateinit var administratorRepository: AdministratorRepository
    @Autowired
    internal lateinit var passwordEncoder: PasswordEncoder

    @Transactional
    fun createUserIfNotExists(login: String, role: UserRole) {
        if (userRepository.findByLogin(login).isPresent) return
        val password = passwordEncoder.encode(login)
        val user = User(login, "$login@$login.$login", password, role)
        val savedUser = userRepository.saveAndFlush(user)
        when (role) {
            UserRole.CLIENT -> {
                clientRepository.saveAndFlush(Client(user = savedUser, isConfirmed = false))
            }
            UserRole.MANAGER -> {
                managerRepository.saveAndFlush(Manager(user = savedUser))
            }
            UserRole.ADMINISTRATOR -> {
                administratorRepository.saveAndFlush(Administrator(user = savedUser))
            }
        }
    }

    override fun run(args: ApplicationArguments?) {
        createUserIfNotExists("admin", UserRole.SYSTEM_ADMIN)
        createUserIfNotExists("client", UserRole.CLIENT)
        createUserIfNotExists("manager", UserRole.MANAGER)
        createUserIfNotExists("administrator", UserRole.ADMINISTRATOR)
    }

}
