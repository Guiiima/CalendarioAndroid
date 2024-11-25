package com.example.calendarios.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarios.model.dao.EventoDAO
import com.example.calendarios.model.entity.Categoria
import com.example.calendarios.model.entity.Evento
import kotlinx.coroutines.launch

class EventoViewModel(private val eventoDAO: EventoDAO): ViewModel() {

    var listaEventos = mutableStateOf(listOf<Evento>())
    var evento = mutableStateOf<Evento?>(null)

    fun buscarEventos(data: String) {
        viewModelScope.launch {
            listaEventos.value = eventoDAO.buscarPorData(data)
        }
    }

    fun salvarEvento(nome: String, data: String, local: String, descricao: String, categoria: Categoria): String {
        if (nome.isBlank()) {
            return "Preencha o nome do Evento!"
        }

        val evento = Evento(id = 0, nome = nome, data = data, local = local, descricao = descricao, categoriaId = categoria.id)

        viewModelScope.launch {
            eventoDAO.inserir(evento)
            buscarEventos(data)
        }

        return "Evento Salvo com Sucesso!"
    }

    fun buscarEventoPorId(id: Int) {
        viewModelScope.launch {
            evento.value = eventoDAO.buscarPorId(id)
        }
    }

    fun buscarPorNome(texto: String) {
        viewModelScope.launch {
            listaEventos.value = eventoDAO.buscarPorNome(texto)
        }
    }

    fun atualizarEvento(evento: Evento): String {
        if (evento.nome.isBlank()) {
            return "Preencha o nome do Evento!"
        }

        viewModelScope.launch {
            eventoDAO.atualizar(evento)
            buscarEventos(evento.data)
        }

        return "Evento Atualizado com Sucesso!"
    }

    fun deletarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDAO.deletar(evento)
            buscarEventos(evento.data)
        }
    }
}
