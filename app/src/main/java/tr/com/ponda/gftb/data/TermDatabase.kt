package tr.com.ponda.gftb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tr.com.ponda.gftb.model.Term

@Database(entities = [Term::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TermDatabase: RoomDatabase() {

    abstract fun termDao(): TermDao
    companion object{
        @Volatile
        private var INSTANCE: TermDatabase? = null

        fun getDatabase(context: Context): TermDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TermDatabase::class.java,
                    "term_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val instance = INSTANCE ?: return@launch
                            val termDao = instance.termDao()
                            try {
                                val jsonString = context.assets.open("terms.json").bufferedReader().use { it.readText() }
                                // Use 'Any' to handle mixed types like numbers and strings in JSON
                                val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
                                val termsAsMaps = Gson().fromJson<List<Map<String, Any>>>(jsonString, listType)

                                val termsToInsert = termsAsMaps.map {
                                    Term(
                                        id = 0, // Let Room auto-generate the ID
                                        term = it["term"] as? String ?: "",
                                        definition = it["definition"] as? String ?: "",
                                        citation = it["citation"] as? String ?: "",
                                        links = (it["links"] as? String)?.split(",")?.map { it.trim() } ?: emptyList(),
                                        figures = (it["figures"] as? String)?.split(",")?.map { it.trim() } ?: emptyList()
                                    )
                                }

                                termDao.addTerm(*termsToInsert.toTypedArray())
                            } catch (e: Exception) { // Catch a wider range of exceptions
                                e.printStackTrace()
                            }
                        }
                    }
                }).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}