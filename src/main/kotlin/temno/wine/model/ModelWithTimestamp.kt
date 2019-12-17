package temno.wine.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.Temporal
import javax.persistence.TemporalType

abstract class ModelWithTimestamp(
        @CreatedDate
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date(),

        @LastModifiedDate
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date()
)
