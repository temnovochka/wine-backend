package temno.wine.model

import javax.persistence.*

@Entity
@Table(name = "_administrator")
data class Administrator(
        var name: String = "",
        var deleted: Boolean = false,

        @ManyToOne(cascade = [CascadeType.MERGE])
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        val user: User
) : ModelWithTimestamp() {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}
