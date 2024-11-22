package com.example.calendarios.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calendarios.model.entity.Categoria

@Dao
interface CategoriaDAO {
    @Insert
    suspend fun inserir(categoria: Categoria)

    @Query("SELECT * FROM categoria")
    suspend fun buscarTodos(): List<Categoria>

    @Query("SELECT * FROM categoria WHERE id = :id")
    suspend fun buscarPorId(id: Int): Categoria

    @Delete
    suspend fun deletar(categoria: Categoria)
}