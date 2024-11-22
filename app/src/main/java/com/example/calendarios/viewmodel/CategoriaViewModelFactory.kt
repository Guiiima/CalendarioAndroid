package com.example.calendarios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calendarios.model.dao.CategoriaDAO

@Suppress("UNCHECKED_CAST")
class CategoriaViewModelFactory(private val categoriaDAO: CategoriaDAO): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            return CategoriaViewModel(categoriaDAO) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida!")
    }
}