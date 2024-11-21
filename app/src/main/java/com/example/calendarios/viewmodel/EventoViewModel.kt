package com.example.calendarios.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendarios.model.dao.EventoDAO
import com.example.calendarios.model.entity.Evento
import kotlinx.coroutines.launch

class EventoViewModel(private val eventoDAO: EventoDAO): ViewModel() {
    var listaEventos = mutableStateOf(listOf<Evento>())

    fun buscarEventos(data: String) {
        viewModelScope.launch {
            listaEventos.value = eventoDAO.buscarPorData(data)
        }
    }

    fun salvarEvento(nome: String, data: String, descricao: String): String {
        if (nome.isBlank()) {
            return "Preencha o nome do Evento!"
        }

        val evento = Evento(id = 0, nome = nome, data = data, descricao = descricao)

        viewModelScope.launch {
            eventoDAO.inserir(evento);
            buscarEventos(data)
        }

        return "Evento Salvo com Sucesso!"
    }

    fun deletarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDAO.deletar(evento)
            buscarEventos(evento.data)
        }
    }
}