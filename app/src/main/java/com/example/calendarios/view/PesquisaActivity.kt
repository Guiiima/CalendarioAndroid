package com.example.calendarios.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendarios.model.database.AppDatabase
import com.example.calendarios.viewmodel.EventoViewModel
import com.example.calendarios.viewmodel.EventoViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class PesquisaActivity : ComponentActivity() {
    private val eventoViewModel: EventoViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).eventoDao()
        EventoViewModelFactory(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PesquisaScreen(eventoViewModel)
        }
    }
}

@Composable
fun PesquisaScreen(eventoViewModel: EventoViewModel) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val eventos by eventoViewModel.listaEventos
//    val eventos = listOf(
//        Pair("Reunião com equipe", "2024-11-25"), // Data exemplo: Hoje
//        Pair("Consulta médica", "2024-11-26"),
//        Pair("Aniversário de João", "2024-11-27"),
//        Pair("Treinamento de segurança", "2024-11-25"), // Data exemplo: Hoje
//        Pair("Jantar com amigos", "2024-11-28"),
//        Pair("Aula de dança", "2024-11-29")
//    )
//
//    val filteredEventos = eventos.filter { it.first.contains(searchText.text, ignoreCase = true) }

    eventoViewModel.buscarPorNome(searchText.text)

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(16.dp)
    ) {
        Text(
            text = "Pesquisar Eventos",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFAFAFA), shape = MaterialTheme.shapes.medium)
                .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                .padding(8.dp)
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color(0xFF333333),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                decorationBox = { innerTextField ->
                    if (searchText.text == "") {
                        Text(
                            text = "Digite aqui",
                            color = Color(0xFF333333).copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(eventos.size) { index ->
                val evento = eventos[index]
                val isToday = evento.data == today
                val containerColor = if (isToday) Color(0xFF3498DB) else Color(0xFF605E5E)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = containerColor)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${evento.nome} - ${evento.data}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
