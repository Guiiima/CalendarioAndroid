package com.example.calendarios.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lembrete")
data class Lembrete(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo("evento_id")
    var eventoId: Int,
    var tempoAntecipado: Int
)
