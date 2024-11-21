package com.example.calendarios

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "calendarView") {
        composable("calendarView") {
            CalendarView(navController)
        }
        composable(
            route = "eventsList/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date").orEmpty()
            EventsListScreen(
                date = date,
                onAddEvent = {
                    navController.navigate("addEvent/$date")
                },
                navController = navController // Passa o NavController
            )
        }

        composable(
            route = "addEvent/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            AddEventScreen(date = date, navController = navController) // Passando o NavController
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(navController: NavController) {
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    val monthsList = remember(currentYear) {
        // Retorna todos os meses do ano, sem restrições
        Month.values().map { it to currentYear }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C)) // Alteração para fundo escuro
    ) {
        YearHeader(currentYear) { direction ->
            currentYear += if (direction == Direction.FORWARD) 1 else -1
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(monthsList) { (month, year) ->
                CalendarMonthView(month, year) { selectedDate ->
                    navController.navigate("eventsList/${selectedDate.toString()}")
                }
            }
        }
    }
}



@Composable
fun YearHeader(currentYear: Int, onDirectionChange: (Direction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF2C2C2C)), // Fundo escuro para o cabeçalho
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { onDirectionChange(Direction.BACKWARD) }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous Year",
                tint = Color.White // Setas brancas
            )
        }

        Text(
            text = "$currentYear",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = { onDirectionChange(Direction.FORWARD) }) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next Year",
                tint = Color.White // Setas brancas
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarMonthView(month: Month, year: Int, onDateSelected: (LocalDate) -> Unit) {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())
    val currentDate = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF484848), shape = RoundedCornerShape(12.dp)) // cor de fundo do calendário
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mês e ano em branco e maiúsculo
        Text(
            text = "${month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).toUpperCase()} $year", // Maiúsculo
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White // Cor do texto em branco
        )

        // A seguir, os outros componentes do calendário
        DaysOfWeekRow()
        DaysOfMonthGrid(firstDayOfMonth, lastDayOfMonth, currentDate, onDateSelected)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaysOfMonthGrid(
    firstDay: LocalDate,
    lastDay: LocalDate,
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfWeek = firstDay.dayOfWeek.value % 7 // Alinhando domingo como 0
    val totalDaysInMonth = lastDay.dayOfMonth

    val totalCells = totalDaysInMonth + firstDayOfWeek // Inclui os dias "vazios" no início
    val rows = (totalCells + 6) / 7 // Calcula o total de linhas necessárias

    var day = 1
    Column {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < firstDayOfWeek || day > totalDaysInMonth) {
                        // Renderiza espaços vazios para os dias fora do mês
                        Spacer(modifier = Modifier.size(45.dp))
                    } else {
                        val dayDate = LocalDate.of(firstDay.year, firstDay.month, day)
                        val isToday = dayDate == currentDate

                        Button(
                            onClick = { onDateSelected(dayDate) },
                            modifier = Modifier
                                .padding(5.dp)
                                .size(35.dp),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isToday) Color(0x88EF5350) else Color(
                                    0xFF605E5E
                                )
                            ),
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 13.sp,
                                color = Color.White // Cor do texto do dia
                            )
                        }
                        day++
                    }
                }
            }
        }
    }
}


    @Composable
    fun EventsListScreen(date: String, onAddEvent: () -> Unit, navController: NavController) {
        // Definindo cores para o tema com tons de preto e azul claro
        val backgroundColor = Color(0xFF1C1C1C) // Fundo preto
        val primaryColor = Color(0xFF3498DB) // Azul claro
        val textColor = Color.White // Cor de texto branca
        val mutedTextColor = Color(0xFFBDC3C7) // Cor para texto mais suave

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor) // Cor de fundo preta para toda a tela
        ) {
            // Barra superior com botão de voltar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2C3E50)) // Azul escuro para a barra superior
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = primaryColor // Azul claro para o ícone
                    )
                }
                Text(
                    text = "Eventos - $date",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp),
                    color = textColor // Texto branco para maior contraste
                )
            }

            // Lista de eventos
            LazyColumn(
                modifier = Modifier.weight(1f) // Ocupa o espaço restante acima do botão
            ) {
                items(listOf("Evento 1", "Evento 2", "Evento 3")) { event ->
                    Text(
                        text = event,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(color = mutedTextColor) // Texto suave para os itens
                    )
                }
            }

        // Botão flutuante para adicionar um evento
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { onAddEvent() },
                containerColor = primaryColor // Azul claro para o botão flutuante
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar evento",
                    tint = Color.White // Ícone branco
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(date: String, navController: NavController) {
    // Definindo cores para o tema com tons de preto e azul claro
    val backgroundColor = Color(0xFF1C1C1C) // Fundo preto
    val fieldBackgroundColor = Color(0xFF2C3E50) // Cor de fundo do campo de texto (azul escuro)
    val primaryColor = Color(0xFF3498DB) // Azul claro
    val textColor = Color.White // Cor de texto branca
    val mutedTextColor = Color(0xFFBDC3C7) // Cor para texto mais suave

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Cor de fundo preta para toda a tela
            .padding(16.dp)
    ) {
        // Barra superior com botão de voltar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = primaryColor
                )
            }
            Text(
                text = "Adicionar Evento",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Exibindo a data de forma mais destacada e bonita
        Text(
            text = "Data Agendada: $date",
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = primaryColor
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo Título
        var title by remember { mutableStateOf("") }
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título do Evento", color = mutedTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor, // Cor de fundo quando em foco
                unfocusedContainerColor = fieldBackgroundColor, // Cor de fundo quando não está em foco
                disabledContainerColor = fieldBackgroundColor, // Cor de fundo quando desabilitado
                focusedIndicatorColor = primaryColor, // Cor do indicador de foco
                unfocusedIndicatorColor = mutedTextColor, // Cor do indicador sem foco
                focusedLabelColor = primaryColor, // Cor do rótulo quando em foco
                unfocusedLabelColor = mutedTextColor // Cor do rótulo sem foco
            ),
            shape = MaterialTheme.shapes.medium
        )

        // Campo Local
        var location by remember { mutableStateOf("") }
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Local", color = mutedTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = mutedTextColor,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = mutedTextColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        // Campo Descrição
        var description by remember { mutableStateOf("") }
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição", color = mutedTextColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .height(150.dp), // Deixa o campo de descrição maior
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = mutedTextColor,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = mutedTextColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        // Botão para salvar o evento
        Button(
            onClick = {
                // Lógica para salvar o evento
                // Pode adicionar lógica para salvar os dados aqui

                navController.popBackStack() // Volta para a tela de eventos
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Salvar Evento", color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}






@Composable
fun DaysOfWeekRow() {
    val daysOfWeek = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB0BEC5) // Cor cinza escuro para os dias da semana
            )
        }
    }
}


enum class Direction {
    FORWARD, BACKWARD
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        AppNavigation()
    }
}
