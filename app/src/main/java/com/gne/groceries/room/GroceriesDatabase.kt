package com.gne.groceries.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gne.groceries.pojo.GroceryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [GroceryData::class],version = 1,exportSchema = false)
abstract class GroceriesDatabase : RoomDatabase() {
    abstract fun daoInterface():DaoInterface

    private class GroceriesDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { groceriesDatabase ->
                scope.launch {
                    // do things required when db is created.
                }
            }
        }
    }

    companion object{
        @Volatile
        private var INSTANCE:GroceriesDatabase?=null

        fun getDatabase(context: Context, scope:CoroutineScope) : GroceriesDatabase{
            val temporaryInstance= INSTANCE
            if(temporaryInstance!=null){
                return temporaryInstance
            }
            synchronized(this){
                val instance= Room.databaseBuilder(context.applicationContext,GroceriesDatabase::class.java,"db_groceries")
                    .addCallback(GroceriesDatabaseCallback(scope))
                    .build()
                INSTANCE=instance
                return instance
            }
        }
    }
}