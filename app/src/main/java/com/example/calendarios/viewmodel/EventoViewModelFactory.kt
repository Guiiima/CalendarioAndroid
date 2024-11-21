package com.example.calendarios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calendarios.model.dao.EventoDAO

@Suppress("UNCHECKED_CAST")
class EventoViewModelFactory(private val eventoDAO: EventoDAO): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
            return EventoViewModel(eventoDAO) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida!")
    }
}