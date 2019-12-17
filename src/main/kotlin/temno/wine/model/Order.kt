package temno.wine.model


import javax.persistence.*

enum class OrderStatus {
    NEW, IN_PROGRESS, DONE, NOT_DONE
}

enum class PaymentStatus {
    PAID, NOT_PAID
}


@Entity
@Table(name = "_order")
data class Order(
        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "client_id", referencedColumnName = "id")
        var client: Client,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "manager_id", referencedColumnName = "id")
        var manager: Manager?,

        @Enumerated(EnumType.STRING)
        var status: OrderStatus,

        @Enumerated(EnumType.STRING)
        var paymentStatus: PaymentStatus
) : ModelWithTimestamp() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}