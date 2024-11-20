package com.example.calendarios

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                CalendarView()
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView() {
    var currentMonth by remember { mutableStateOf(LocalDate.now().month) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // A lista de meses precisa ser recalculada com base no ano e mês atuais
    val monthsList = remember(currentYear) {
        val list = mutableListOf<Pair<Month, Int>>()
        // Preenche a lista com meses a partir do mês atual no ano atual
        if (currentYear == LocalDate.now().year) {
            for (month in Month.values()) {
                if (month.ordinal >= LocalDate.now().monthValue - 1) {
                    list.add(month to currentYear)
                }
            }
        } else {
            for (month in Month.values()) {
                list.add(month to currentYear)
            }
        }
        list
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Cabeçalho fixo para a navegação entre os anos
        YearHeader(currentYear, currentMonth) { direction ->
            if (direction == Direction.FORWARD) {
                currentYear++
            } else if (direction == Direction.BACKWARD && currentYear > LocalDate.now().year) {
                currentYear--
            }
        }

        // LazyColumn para a rolagem da lista de meses
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(monthsList) { (month, year) ->
                CalendarMonthView(month, year) { date ->
                    // Atualiza o estado da data selecionada
                    selectedDate = date
                }
            }
        }
    }

    // Exibe a data selecionada (somente para fins de teste)
    if (selectedDate != null) {
        showSelectedDate(selectedDate!!)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearHeader(currentYear: Int, currentMonth: Month, onYearChanged: (Direction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onYearChanged(Direction.BACKWARD) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
        }

        Text(
            text = "$currentYear",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF455A64)
        )

        IconButton(
            onClick = { onYearChanged(Direction.FORWARD) },
            modifier = Modifier.size(40.dp)
        ) {
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
    val firstDayOfWeek = firstDay.dayOfWeek.value % 7
    val totalDaysInMonth = lastDay.dayOfMonth

    var day = 1
    for (i in 0 until (totalDaysInMonth + firstDayOfWeek + 6) / 7) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (j in 0..6) {
                if (i == 0 && j < firstDayOfWeek || day > totalDaysInMonth) {
                    Spacer(modifier = Modifier.size(45.dp))
                } else {
                    val dayDate = LocalDate.of(firstDay.year, firstDay.month, day)
                    val isToday = dayDate == currentDate
                    val isDisabled = dayDate.isBefore(currentDate)

                    Button(
                        onClick = {
                            if (!isDisabled) onDateSelected(dayDate)
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .size(35.dp),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isDisabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isToday -> Color(0xFF80C8FF) // Cor para o dia de hoje
                                else -> Color(0xFFB0BEC5) // Cor padrão
                            }
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showSelectedDate(date: LocalDate) {
    Text(
        text = "Data selecionada: ${date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("pt", "BR")))}",
        style = MaterialTheme.typography.bodySmall
    )
}

enum class Direction {
    FORWARD, BACKWARD
}

@Composable
fun DaysOfWeekRow() {
    // Lista de dias da semana em português
    val diasSemana = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        diasSemana.forEach { dia ->
            Text(
                text = dia,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                style = customBody14.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = Color(0xFF607D8B) // Tom suave de cinza azulado
            )
        }
    }
}

val customBody14 = androidx.compose.ui.text.TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    color = Color(0xFFB0BEC5) // Tom suave de cinza
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        CalendarView()
    }
}
