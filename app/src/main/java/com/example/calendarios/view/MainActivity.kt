package com.example.calendarios.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.calendarios.model.database.AppDatabase
import com.example.calendarios.model.entity.Evento
import com.example.calendarios.viewmodel.CategoriaViewModel
import com.example.calendarios.viewmodel.CategoriaViewModelFactory
import com.example.calendarios.viewmodel.EventoViewModel
import com.example.calendarios.viewmodel.EventoViewModelFactory
import java.time.Clock
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val eventoViewModel: EventoViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).eventoDao()
        EventoViewModelFactory(dao)
    }
    private val categoriaViewModel: CategoriaViewModel by viewModels {
        val dao = AppDatabase.getDatabase(applicationContext).categoriaDao()
        CategoriaViewModelFactory(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation(eventoViewModel, categoriaViewModel)

            }
        }
    }
}

@Composable
fun AppNavigation(eventoViewModel: EventoViewModel, categoriaViewModel: CategoriaViewModel) {
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
                navController = navController,
                eventoViewModel = eventoViewModel
            )
        }
        composable(
            route = "addEvent/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            AddEventScreen(
                date = date,
                navController = navController,
                eventoViewModel = eventoViewModel,
                categoriaViewModel = categoriaViewModel
            )
        }
        composable(
            route = "editar_evento/{eventoId}/{data}",
            arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventoId = backStackEntry.arguments?.getInt("eventoId")
            val data = backStackEntry.arguments?.getString("data") ?: null.toString()
            if (eventoId != null) {
                AddEventScreen(
                    date = data,
                    navController = navController,
                    eventoViewModel = eventoViewModel,
                    categoriaViewModel = categoriaViewModel,
                    eventoId = eventoId
                )
            }
        }
    }
}


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
    IconButtonWithDropdown()
}

@Composable
fun YearHeader(currentYear: Int, onDirectionChange: (Direction) -> Unit) {
    val context = LocalContext.current

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
@Composable
fun IconButtonWithDropdown() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) } // Controla se o menu está visível
    val options = listOf("Cadastrar Categoria") // Opções do menu

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.large,
            containerColor = Color(0xFF5A9BD5).copy(alpha = 0.8f), // Azul mais suave
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(start = 16.dp, end = 32.dp, bottom = 32.dp) // Padding adicional à direita e embaixo
                .size(50.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Categorias",
                tint = Color.White,
                modifier = Modifier.size(32.dp) // Tamanho do ícone
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-16).dp, y = 8.dp),
            modifier = Modifier.background(Color(0xFF3498DB).copy(alpha = 0.6f))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        if (option == "Cadastrar Categoria") {
                            val intent = Intent(context, CategoriaActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    text = {
                        Text(
                            text = option,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }
    }
}









@Composable
fun CalendarMonthView(month: Month, year: Int, onDateSelected: (LocalDate) -> Unit) {
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())
    val currentDate = LocalDate.now(Clock.systemUTC())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF484848), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${
                month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).toUpperCase()
            } $year",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White
        )
        DaysOfWeekRow()
        DaysOfMonthGrid(firstDayOfMonth, lastDayOfMonth, currentDate, onDateSelected)
    }
}


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
fun EventsListScreen(
    date: String,
    onAddEvent: () -> Unit,
    navController: NavController,
    eventoViewModel: EventoViewModel
) {
    val backgroundColor = Color(0xFF1C1C1C)
    val primaryColor = Color(0xFF3498DB)
    val textColor = Color.White

    var listaEventos by eventoViewModel.listaEventos
    eventoViewModel.buscarEventos(date)

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
            items(listaEventos) { event ->
                EventoView(
                    evento = event,
                    eventoViewModel = eventoViewModel,
                    navController = navController
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
fun AddEventScreen(
    date: String,
    navController: NavController,
    eventoViewModel: EventoViewModel,
    categoriaViewModel: CategoriaViewModel,
    eventoId: Int? = null
) {
    val backgroundColor = Color(0xFF1C1C1C)
    val fieldBackgroundColor = Color(0xFF2C3E50)
    val primaryColor = Color(0xFF3498DB)
    val textColor = Color.White
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

    val formattedDate = if (date != "null") {
        LocalDate.parse(date).format(formatter)
    } else {
        "Data não disponível"
    }
    var nome by remember { mutableStateOf("") }
    var local by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by categoriaViewModel.categoria
    var categoriaId: Int
    var isInitialized by remember { mutableStateOf(false) }

    var categorias by categoriaViewModel.listaCategorias
    categoriaViewModel.buscarTodasCategorias()

    if (eventoId != null) {
        eventoViewModel.buscarEventoPorId(eventoId)
    }
    val evento by eventoViewModel.evento
    if (evento != null && !isInitialized) {
        evento?.let {
            nome = it.nome
            descricao = it.descricao
            local = it.local
            categoriaId = it.categoriaId
            categoriaViewModel.buscarCategoriaPorId(categoriaId)
        }
        isInitialized = true
    }

    var expanded by remember { mutableStateOf(false) }

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
                text = if (eventoId != null) "Editar Evento" else "Adicionar Evento",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (formattedDate != null) {
            Text(
                text = formattedDate,
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Título do Evento", color = textColor) },
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
                unfocusedLabelColor = textColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        TextField(
            value = local,
            onValueChange = { local = it },
            label = { Text("Local", color = textColor) },
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
                unfocusedLabelColor = textColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        TextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição", color = textColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(150.dp),
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackgroundColor,
                unfocusedContainerColor = fieldBackgroundColor,
                disabledContainerColor = fieldBackgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor
            ),
            shape = MaterialTheme.shapes.medium
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            OutlinedTextField(
                value = categoria.nome,
                onValueChange = { categoria.nome = it },
                label = { Text("Categoria", color = textColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Abrir menu de categorias",
                        tint = textColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBackgroundColor,
                    unfocusedContainerColor = fieldBackgroundColor,
                    disabledContainerColor = fieldBackgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor,

                ),
                interactionSource = interactionSource
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = fieldBackgroundColor)
            ) {
                if (categorias.isEmpty()) {
                    Text("Nenhuma categoria disponível", modifier = Modifier.padding(8.dp))
                } else {
                    categorias.forEach { categoriaOption ->
                        DropdownMenuItem(
                            onClick = {
                                categoria = categoriaOption
                                expanded = false
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically, // Alinha os itens verticalmente no centro
                                    horizontalArrangement = Arrangement.Start, // Alinha os itens à esquerda
                                    modifier = Modifier.fillMaxWidth() // Preenche a largura do item
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(1.dp, 40.dp) // Ajuste o tamanho da linha
                                            .background(
                                                Color(categoriaOption.cor)
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(16.dp)) // Ajusta o espaçamento entre a linha e o texto
                                    Text(
                                        text = categoriaOption.nome,
                                        color = textColor
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (eventoId != null) {
                    val eventoAtualizado = evento?.copy(
                        nome = nome,
                        local = local,
                        descricao = descricao,
                        categoriaId = categoria.id
                    )
                    eventoAtualizado?.let {
                        eventoViewModel.atualizarEvento(it)
                        navController.popBackStack()
                    }
                } else {
                    eventoViewModel.salvarEvento(nome, date, local, descricao, categoria)
                    navController.popBackStack()
                }
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
            Text(
                if (eventoId != null) "Atualizar Evento" else "Salvar Evento",
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
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

@Composable
fun EventoView(evento: Evento, eventoViewModel: EventoViewModel, navController: NavController) {
    val mutedTextColor = Color(0xFFBDC3C7)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        navController.navigate("editar_evento/${evento.id}/${evento.data}")

                    }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = evento.nome,
                style = MaterialTheme.typography.bodyLarge.copy(color = mutedTextColor)
            )
            Text(
                text = evento.descricao,
                style = MaterialTheme.typography.bodyMedium.copy(color = mutedTextColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                eventoViewModel.deletarEvento(evento)
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

enum class Direction {
    FORWARD, BACKWARD
}

