package temno.wine.model

import javax.persistence.*

@Entity
@Table(name = "_stock")
data class Stock(
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        var product: Product,

        var number: Int,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "manager_id", referencedColumnName = "id")
        var manager: Manager
) : ModelWithTimestamp() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}