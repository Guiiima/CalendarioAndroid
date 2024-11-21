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

    @Delete
    suspend fun deletar(categoria: Categoria)
}