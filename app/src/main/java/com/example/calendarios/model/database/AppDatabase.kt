package com.example.calendarios.model.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calendarios.model.dao.CategoriaDAO
import com.example.calendarios.model.dao.EventoDAO
import com.example.calendarios.model.dao.LembreteDAO
import com.example.calendarios.model.entity.Categoria
import com.example.calendarios.model.entity.Evento
import com.example.calendarios.model.entity.Lembrete

@Database(
    entities = [Evento::class, Categoria::class, Lembrete::class],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventoDao() : EventoDAO
    abstract fun categoriaDao() : CategoriaDAO
    abstract fun lembreteDao(): LembreteDAO

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