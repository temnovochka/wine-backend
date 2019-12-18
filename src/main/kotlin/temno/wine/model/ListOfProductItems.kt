package temno.wine.model

import javax.persistence.*

@Entity
@Table(name = "_list_of_product_items")
data class ListOfProductItems(
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "product_id", referencedColumnName = "id")
        var product: Product,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "order_id", referencedColumnName = "id")
        var order: Order?,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "purchase_id", referencedColumnName = "id")
        var purchase: Purchase?,

        var number: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}