package com.example.calendarios.model.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calendarios.model.dao.CategoriaDAO
import com.example.calendarios.model.dao.EventoDAO
import com.example.calendarios.model.entity.Categoria
import com.example.calendarios.model.entity.Evento

@Database(
    entities = [Evento::class, Categoria::class],
    version = 6,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventoDao() : EventoDAO
    abstract fun categoriaDao() : CategoriaDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}