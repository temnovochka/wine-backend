package temno.wine.model

import javax.persistence.*
import javax.validation.constraints.NotBlank


@Entity
@Table(name = "_product")
data class Product(
        @NotBlank
        var name: String,

        var features: String,

        var price: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}