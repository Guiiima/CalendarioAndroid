package com.example.calendarios.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarios.model.dao.CategoriaDAO
import com.example.calendarios.model.entity.Categoria
import kotlinx.coroutines.launch

class CategoriaViewModel(private val categoriaDAO: CategoriaDAO): ViewModel() {
    var listaCategorias = mutableStateOf(listOf<Categoria>())
    var categoria = mutableStateOf(Categoria())

    init {
        buscarTodasCategorias()
    }
    
    fun buscarTodasCategorias() {
        viewModelScope.launch { 
            listaCategorias.value = categoriaDAO.buscarTodos()
        }
    }
    
    fun salvarCategoria(nome: String, cor: Int): Boolean {
        if (nome.isBlank()) {
            return false
        }
        
        val categoria = Categoria(id = 0, nome = nome, cor = cor)
        
        viewModelScope.launch { 
            categoriaDAO.inserir(categoria)
            buscarTodasCategorias()
        }
        
        return true
    }

    fun buscarCategoriaPorId(id: Int) {
        viewModelScope.launch {
            categoria.value = categoriaDAO.buscarPorId(id)
        }
    }

    fun deletarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            categoriaDAO.deletar(categoria)
            buscarTodasCategorias()
        }
    }
}