package com.example.calendarios.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarios.model.dao.CategoriaDAO
import com.example.calendarios.model.entity.Categoria
import kotlinx.coroutines.launch

class CategoriaViewModel(private val categoriaDAO: CategoriaDAO): ViewModel() {
    var listaCategorias = mutableStateOf(listOf<Categoria>())
    
    private fun buscarTodasCategorias() {
        viewModelScope.launch { 
            listaCategorias.value = categoriaDAO.buscarTodos()
        }
    }
    
    fun salvarCategoria(nome: String, cor: String = "Verde"): Boolean {
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
}