package com.example.calendarios.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calendarios.model.entity.Lembrete

@Dao
interface LembreteDAO {
    @Insert
    suspend fun inserir(lembrete: Lembrete)

    @Query("SELECT * FROM lembrete WHERE evento_id = :idEvento")
    suspend fun buscarPorEvento(idEvento: Int): List<Lembrete>

    @Delete
    suspend fun deletar(lembrete: Lembrete)
}