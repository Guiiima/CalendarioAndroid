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

    Column(modifier = Modifier.fillMaxSize()) {
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearHeader(currentYear: Int, onYearChanged: (Direction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onYearChanged(Direction.BACKWARD) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
        }

        Text(
            text = "$currentYear",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF455A64)
        )

        IconButton(onClick = { onYearChanged(Direction.FORWARD) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Avançar")
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
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} $year",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color(0xFF455A64)
        )

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
                        val isDisabled = dayDate.isBefore(currentDate)

                        Button(
                            onClick = { if (!isDisabled) onDateSelected(dayDate) },
                            modifier = Modifier
                                .padding(5.dp)
                                .size(35.dp),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(0.dp),
                            enabled = !isDisabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isToday) Color(0xFF80C8FF) else Color(0xFFB0BEC5) // Cor padrão
                            )
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 13.sp,
                                color = if (isToday) Color.White else Color(0xFF263238)
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra superior com botão de voltar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF455A64)
                )
            }
            Text(
                text = "Eventos - $date",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp),
                color = Color(0xFF455A64)
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
                    style = MaterialTheme.typography.bodyLarge
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
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar evento",
                    tint = Color.White
                )
            }
        }
    }
}



@Composable
fun AddEventScreen(date: String, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Adicionar Evento",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Formulário para adicionar eventos
        Text(
            text = "Data: $date",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campos do formulário (Exemplo simples)
        TextField(
            value = "",
            onValueChange = { /* Atualize o valor do campo */ },
            label = { Text("Título do Evento") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        TextField(
            value = "",
            onValueChange = { /* Atualize o valor do campo */ },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Botão para salvar o evento
        Button(
            onClick = {
                // Lógica para salvar o evento
                navController.popBackStack() // Volta para a tela de eventos
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Evento")
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
                fontWeight = FontWeight.Bold
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
