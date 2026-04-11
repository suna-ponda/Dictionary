package tr.com.ponda.gftb.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "term_table")
data class Term(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val term: String,
    val definition: String,
    val citation: String,
    val links: List<String>,
    val figures: List<String>
): Parcelable
