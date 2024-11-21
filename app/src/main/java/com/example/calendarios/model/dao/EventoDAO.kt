package com.example.calendarios.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calendarios.model.entity.Evento
import java.util.Date

@Dao
interface EventoDAO {
    @Insert
    suspend fun inserir(evento: Evento)

    @Query("SELECT * FROM evento")
    suspend fun buscarTodos(): List<Evento>

    @Query("SELECT * FROM evento WHERE data = :data")
    suspend fun buscarPorData(data: String): List<Evento>

    @Delete
    suspend fun deletar(evento: Evento)
}