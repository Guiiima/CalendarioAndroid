package com.example.calendarios.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calendarios.model.database.AppDatabase
import com.example.calendarios.viewmodel.CategoriaViewModel
import com.example.calendarios.viewmodel.CategoriaViewModelFactory

class CategoriaActivity : ComponentActivity() {
    private val categoriaViewModel: CategoriaViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).categoriaDao()
        CategoriaViewModelFactory(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CadastroCategoriaScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CadastroCategoriaScreen() {
    var nomeCategoria by remember { mutableStateOf("") }
    var corSelecionada by remember { mutableStateOf(Color.Green) }

    val cores = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta,
        Color.Cyan, Color.Gray, Color.Black, Color.White, Color(0xFFFFA500), // Laranja
        Color(0xFF800080), // Roxo escuro
        Color(0xFF808080), // Cinza claro
        Color(0xFF000080)  // Azul marinho
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo de nome da categoria
        TextField(
            value = nomeCategoria,
            onValueChange = { nomeCategoria = it },
            label = { Text("Nome da Categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        // Campo de seleção de cores
        Text(
            text = "Selecione uma cor:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Exibe as opções de cores
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            cores.forEach { cor ->
                Box(
                    modifier = Modifier
                        .size(32.dp) // Tamanho menor dos círculos
                        .clip(CircleShape)
                        .background(cor)
                        .border(
                            width = 2.dp,
                            color = if (cor == corSelecionada) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { corSelecionada = cor } // Seleção da cor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de Salvar
        Button(
            onClick = { /* Ação ao salvar */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Salvar")
        }
    }
}

