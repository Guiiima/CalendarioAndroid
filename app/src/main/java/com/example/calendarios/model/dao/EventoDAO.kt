package com.example.calendarios.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.calendarios.model.entity.Evento

@Dao
interface EventoDAO {
    @Insert
    suspend fun inserir(evento: Evento)

    @Query("SELECT * FROM evento")
    suspend fun buscarTodos(): List<Evento>

    @Query("SELECT * FROM evento WHERE data = :data")
    suspend fun buscarPorData(data: String): List<Evento>

    @Query("SELECT * FROM evento WHERE id = :id")
    suspend fun buscarPorId(id: Int): Evento

    @Update
    suspend fun atualizar(evento: Evento)

    @Delete
    suspend fun deletar(evento: Evento)
}
