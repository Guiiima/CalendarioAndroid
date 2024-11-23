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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                AppNavigation(categoriaViewModel = categoriaViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(categoriaViewModel: CategoriaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "categoriasList") {
        composable("categoriasList") {
            CategoriasListScreen(
                onAddEvent = {
                    navController.navigate("addCategoria")
                },
                categoriaViewModel = categoriaViewModel,
            )
        }

        composable("addCategoria") {
            CadastroCategoriaScreen(categoriaViewModel, navController)
        }
    }
}

@Composable
fun CategoriasListScreen(onAddEvent: () -> Unit, categoriaViewModel: CategoriaViewModel) {
    val backgroundColor = Color(0xFF1C1C1C)
    val primaryColor = Color(0xFF3498DB)
    val textColor = Color.White

    var listaCategorias by categoriaViewModel.listaCategorias
    categoriaViewModel.buscarTodasCategorias()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C3E50))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categorias",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 8.dp),
                color = textColor
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(listaCategorias) { categoria ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Alinha os itens verticalmente no centro
                    horizontalArrangement = Arrangement.Start, // Alinha os itens à esquerda
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(1.dp, 40.dp) // Ajuste o tamanho da linha
                            .background(
                                Color(categoria.cor)
                            )
                    )
                    Spacer(modifier = Modifier.width(16.dp)) // Ajusta o espaçamento entre a linha e o texto
                    Text(
                        text = categoria.nome,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            categoriaViewModel.deletarCategoria(categoria)
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar Evento",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { onAddEvent() },
                containerColor = primaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar categoria",
                    tint = Color.White
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CadastroCategoriaScreen(
    categoriaViewModel: CategoriaViewModel,
    navController: NavController,
) {
    val backgroundColor = Color(0xFF1C1C1C)  // Cor de fundo
    val primaryColor = Color(0xFF3498DB)  // Cor principal (botões e ícones)
    val textColor = Color.White  // Cor do texto
    val fieldBackgroundColor = Color(0xFF2C3E50)  // Cor de fundo dos campos

    var nomeCategoria by remember { mutableStateOf("") }
    var corSelecionada by remember { mutableStateOf(Color.Green) }

    val cores = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta,
        Color.Cyan, Color.Gray, Color.Black, Color.White, Color(0xFFFFA500),
        Color(0xFF800080),
        Color(0xFF808080),
        Color(0xFF000080)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(16.dp)
    ) {
        TextField(
            value = nomeCategoria,
            onValueChange = { nomeCategoria = it },
            label = { Text("Nome da Categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        Text(
            text = "Selecione uma cor:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = textColor
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            cores.forEach { cor ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(cor)
                        .border(
                            width = 2.dp,
                            color = if (cor == corSelecionada) Color.Black else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { corSelecionada = cor }
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                categoriaViewModel.salvarCategoria(nomeCategoria, corSelecionada.toArgb())
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text(text = "Salvar")
        }
    }
}