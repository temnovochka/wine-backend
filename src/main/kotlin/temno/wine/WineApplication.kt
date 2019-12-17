package temno.wine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters

@SpringBootApplication
@EntityScan(basePackageClasses = [Jsr310JpaConverters::class, WineApplication::class])
class WineApplication

fun main(args: Array<String>) {
    runApplication<WineApplication>(*args)
}
