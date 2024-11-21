package com.example.calendarios.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evento")
data class Evento(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nome: String,
    var data: String,
    var descricao: String,
//    var recorrente: Boolean,
//    var categoria: Categoria,
)
