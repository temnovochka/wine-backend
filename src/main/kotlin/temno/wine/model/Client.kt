package temno.wine.model

import javax.persistence.*

@Entity
@Table(name = "_client")
data class Client(
        var name: String = "",
        var document: String = "",
        var birthday: String = "",

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        val user: User,

        var isConfirmed: Boolean = false

) : ModelWithTimestamp() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
