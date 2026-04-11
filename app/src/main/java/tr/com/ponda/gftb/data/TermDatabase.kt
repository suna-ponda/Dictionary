package tr.com.ponda.gftb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}