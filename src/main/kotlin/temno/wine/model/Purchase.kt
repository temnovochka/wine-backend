package temno.wine.model

import javax.persistence.*

@Entity
@Table(name = "_purchase")
data class Purchase(
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "manager_id", referencedColumnName = "id")
        var manager: Manager,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "administrator_id", referencedColumnName = "id")
        var administrator: Administrator?,

        var supplier: String?,

        @Enumerated(EnumType.STRING)
        var status: OrderStatus,

        var isAddedIntoStock: Boolean = false
) : ModelWithTimestamp() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}