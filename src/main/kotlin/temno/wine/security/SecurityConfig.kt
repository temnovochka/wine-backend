package temno.wine.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import temno.wine.model.User
import temno.wine.repository.UserRepository


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var unauthorizedHandler: JwtAuthenticationEntryPoint

    @Autowired
    private lateinit var userRepository: UserRepository

    @Bean
    fun jwtAuthenticationFilter() = JwtAuthenticationFilter()


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean() = AuthenticationManager { authentication ->
        val principal = authentication.principal
        if (principal is User) return@AuthenticationManager principal
        if (principal !is String) throw IllegalStateException("Unexpected principal: $principal")
        val login = authentication.principal as String
        val password = authentication.credentials as String

        val candidate = userRepository.findByLogin(login).orElseThrow { throw BadCredentialsException("No such user: $login") }
        if (!passwordEncoder().matches(password, candidate.password)) {
            throw BadCredentialsException("Incorrect password")
        }
        candidate
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated()

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val corsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS)
        corsConfiguration.addAllowedMethod(HttpMethod.PUT)
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}
