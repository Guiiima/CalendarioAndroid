package com.example.calendarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CalendarView()
            }
        }
    }
}

@Composable
fun CalendarView() {
    var currentMonth by remember { mutableStateOf(LocalDate.now().month) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    val monthsList = remember { mutableStateListOf<Pair<Month, Int>>() }
    monthsList.add(currentMonth to currentYear)

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = rememberLazyListState()
    ) {
        items(monthsList) { (month, year) ->
            CalendarMonthView(month, year)

            if (monthsList.indexOf(month to year) == monthsList.size - 1) {
                LaunchedEffect(monthsList) {
                    val lastItem = monthsList.last()
                    val nextMonth = lastItem.first.plus(1)
                    val nextYear = if (nextMonth == Month.JANUARY) lastItem.second + 1 else lastItem.second
                    monthsList.add(nextMonth to nextYear)
                }
            }
        }
    }
}

val customBody14 = androidx.compose.ui.text.TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    color = Color(0xFFB0BEC5) // Tom suave de cinza
)

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

@Composable
fun CalendarMonthView(month: Month, year: Int) {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())
    val currentDate = LocalDate.now()
    val currentDay = currentDate.dayOfMonth // Dia de hoje
    val currentMonth = currentDate.month // Mês atual
    val currentYear = currentDate.year // Ano atual

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)) // Cor de fundo mais suave
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título do mês em português
        Text(
            text = "${month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))} $year",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(12.dp),
            color = Color(0xFF455A64) // Cor de texto suave
        )
        DaysOfWeekRow()
        DaysOfMonthGrid(firstDayOfMonth, lastDayOfMonth, currentDay, currentMonth, currentYear)
    }
}

@Composable
fun DaysOfMonthGrid(firstDay: LocalDate, lastDay: LocalDate, currentDay: Int, currentMonth: Month, currentYear: Int) {
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
                    // Verificar se o dia é o de hoje e destacar com vermelho apenas se for do mês atual
                    val isToday = day == currentDay && currentMonth == LocalDate.now().month && currentYear == LocalDate.now().year
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .padding(5.dp)
                            .size(35.dp),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF80C8FF) // Vermelho para o dia de hoje no mês atual
                        )
                    ) {
                        Text(
                            text = "$day",
                            fontSize = 13.sp,
                            color = if (isToday) Color.White else Color(0xFF263238) // Branco para o dia de hoje, texto escuro para os outros
                        )
                    }
                    day++
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        CalendarView()
    }
}
