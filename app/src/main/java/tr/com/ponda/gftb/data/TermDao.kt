package tr.com.ponda.gftb.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import tr.com.ponda.gftb.model.Term

@Dao
interface TermDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTerm(vararg term: Term)
    @Update
    suspend fun updateTerm(term: Term)

    @Delete
    suspend fun deleteTerm(term: Term)

    @Query("DELETE FROM term_table")
    suspend fun deleteAllTerms()

    @Query("SELECT * FROM term_table ORDER BY term ASC")
    fun readAllData() : LiveData<List<Term>>

    @Query("SELECT * FROM term_table WHERE term LIKE :searchQuery ORDER BY term ASC")
    fun searchDatabase(searchQuery:String) : LiveData<List<Term>>

    @Query("SELECT * FROM term_table WHERE term = :name COLLATE NOCASE")
    fun getTermByName(name: String): LiveData<Term?>
}