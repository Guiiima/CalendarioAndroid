package com.example.calendarios.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nome: String,
    var cor: String
)
