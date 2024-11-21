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
                navController = navController
            )
        }
        composable(
            route = "addEvent/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            AddEventScreen(date = date, navController = navController)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(navController: NavController) {
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    val monthsList = remember(currentYear) {
        Month.values().map { it to currentYear }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
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
            .background(Color(0xFF2C2C2C)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { onDirectionChange(Direction.BACKWARD) }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Ano Anterior",
                tint = Color.White
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
                contentDescription = "Proximo Ano",
                tint = Color.White
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
            .background(Color(0xFF484848), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).toUpperCase()} $year",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White
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
    val firstDayOfWeek = firstDay.dayOfWeek.value % 7
    val totalDaysInMonth = lastDay.dayOfMonth

    val totalCells = totalDaysInMonth + firstDayOfWeek
    val rows = (totalCells + 6) / 7

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
                                containerColor = if (isToday) Color(0xFF3498DB) else Color(
                                    0xFF605E5E
                                )
                            ),
                        ) {
                            Text(
                                text = "$day",
                                fontSize = 13.sp,
                                color = Color.White
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
        val backgroundColor = Color(0xFF1C1C1C)
        val primaryColor = Color(0xFF3498DB)
        val textColor = Color.White
        val mutedTextColor = Color(0xFFBDC3C7)

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
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = primaryColor
                    )
                }
                Text(
                    text = "Eventos - $date",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 8.dp),
                    color = textColor
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(listOf("Evento 1", "Evento 2", "Evento 3")) { event ->
                    Text(
                        text = event,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(color = mutedTextColor)
                    )
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
                    contentDescription = "Adicionar evento",
                    tint = Color.White
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(date: String, navController: NavController) {
    val backgroundColor = Color(0xFF1C1C1C)
    val fieldBackgroundColor = Color(0xFF2C3E50)
    val primaryColor = Color(0xFF3498DB)
    val textColor = Color.White
    val mutedTextColor = Color(0xFFBDC3C7)
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
    val formattedDate = LocalDate.parse(date).format(formatter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

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

        Text(
            text = "$formattedDate",
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
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = mutedTextColor
            ),
            shape = MaterialTheme.shapes.medium
        )

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
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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
                .height(150.dp),
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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

                navController.popBackStack()
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
                color = Color(0xFFB0BEC5)
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
