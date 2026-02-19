package com.example.pocket20

import kotlinx.coroutines.delay
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import com.example.pocket20.ui.theme.Pocket20Theme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout

enum class Screen {

    Shop, Profile

}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            Pocket20Theme {

                var currentScreen by remember { mutableStateOf(Screen.Shop) }

                var globalCount by remember { mutableStateOf(1L) }

                var shopLevel by remember { mutableStateOf(1) }

                var speedCoef by remember { mutableStateOf(1) }

                var isWon by remember { mutableStateOf(false) }

                var rebirth by remember { mutableStateOf(1) }


                val items = listOf("Shop", "Profile")

                val icons = listOf(Icons.Filled.Home, Icons.Filled.Person)

                val winScore = 1_000_000L


                LaunchedEffect(speedCoef, isWon) {

                    while (!isWon) {

                        val safeSpeed = if (speedCoef <= 0) 1 else speedCoef

                        val realDelay = 1000L / safeSpeed

                        delay(realDelay)

                        globalCount += shopLevel


                        if (globalCount >= winScore) {

                            isWon = true

                        }

                    }

                }


                Scaffold(

                    modifier = Modifier.fillMaxSize(),

                    containerColor = MaterialTheme.colorScheme.background,

                    bottomBar = {

                        if (!isWon) {

                            NavigationBar {

                                items.forEachIndexed { index, item ->

                                    NavigationBarItem(

                                        icon = { Icon(icons[index], contentDescription = item) },

                                        label = { Text(item) },

                                        selected = currentScreen.ordinal == index,

                                        onClick = {

                                            currentScreen = when (index) {

                                                0 -> Screen.Shop

                                                1 -> Screen.Profile

                                                else -> Screen.Shop

                                            }

                                        }

                                    )

                                }

                            }

                        }

                    }

                ) { innerPadding ->

                    Surface(

                        modifier = Modifier

                            .fillMaxSize()

                            .padding(innerPadding),

                        color = MaterialTheme.colorScheme.surface

                    ) {

                        if (isWon) {

                            WinScreen(

                                globalCount = globalCount,

                                onRestart = {

                                    globalCount = 0L

                                    shopLevel = 1

                                    speedCoef = 1

                                    rebirth++

                                    isWon = false

                                    currentScreen = Screen.Shop

                                })

                        } else {

                            when (currentScreen) {

                                Screen.Shop -> ShopScreen(

                                    count = globalCount,

                                    onCountChange = { globalCount = it },

                                    level = shopLevel,

                                    onLevelChange = { shopLevel = it },

                                    speedCoef = speedCoef,

                                    onSpeedChange = { speedCoef = it }

                                )


                                Screen.Profile -> ProfileScreen(

                                    currentCount = globalCount,

                                    onCountChange = { globalCount = it },

                                    onWinChange = { isWon = it },

                                    rebirth = rebirth

                                )

                            }

                        }

                    }

                }

            }

        }

    }

}


@Composable

fun ShopScreen(

    count: Long,

    onCountChange: (Long) -> Unit,

    level: Int,

    onLevelChange: (Int) -> Unit,

    speedCoef: Int,

    onSpeedChange: (Int) -> Unit

) {

    Column(

        modifier = Modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Top

    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Text(text = "Level: $level", fontSize = 40.sp)

        Spacer(modifier = Modifier.height(150.dp))

        Text(text = "Speed Coef: $speedCoef", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Score: $count", fontSize = 30.sp)


        Spacer(modifier = Modifier.height(40.dp))


        Button(

            onClick = {

                val currentCost = (10 * level).toLong()

                if (count >= currentCost) {

                    onCountChange(count - currentCost)

                    onLevelChange(level + 1)

                }

            },

            shape = RoundedCornerShape(8.dp), // Указываем форму

            modifier = Modifier

                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)) // Возвращаем тень

                .size(200.dp, 80.dp)

        ) {

            Text(text = "Upgrade Lvl\n(${10 * level})", textAlign = TextAlign.Center)

        }


        Spacer(modifier = Modifier.height(20.dp))


        Button(

            onClick = {

                val currentCostSpeed = (100 * speedCoef).toLong()

                if (count >= currentCostSpeed) {

                    onCountChange(count - currentCostSpeed)

                    onSpeedChange(speedCoef + 1)

                }

            },

            shape = RoundedCornerShape(8.dp), // Указываем форму

            modifier = Modifier

                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)) // Возвращаем тень

                .size(200.dp, 80.dp)

        ) {

            Text(text = "SpeedUp\n(${100 * speedCoef})", textAlign = TextAlign.Center)

        }

    }

}


@Composable
fun ProfileScreen(
    currentCount: Long,
    onCountChange: (Long) -> Unit,
    onWinChange: (Boolean) -> Unit,
    rebirth: Int
) {
    var clickCount by remember { mutableStateOf(0) }
    var consoleVisible by remember { mutableStateOf(false) }
    var consoleHistory by remember { mutableStateOf("Console initialized...") }
    var commandText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (rebirth > 1) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    clickCount++
                    if (clickCount > 4) consoleVisible = true
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "The End?")
                Text(text = "Get 1,000,000 score.", fontSize = 16.sp)
                }
            }
        }


        if (consoleVisible) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Developer console", color = Color.Gray, fontSize = 12.sp)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                border = BorderStroke(1.dp, Color.Green)
            ) {
                // 1. Используем Column как основной контейнер внутри Card
                Column(modifier = Modifier.fillMaxSize()) {

                    // 2. Обертка для логов и кнопки закрытия
                    Box(modifier = Modifier
                        .weight(1f) // Занимает всё доступное пространство, толкая TextField вниз
                        .fillMaxWidth()
                    ) {
                        // Прокручиваемый список истории
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = consoleHistory,
                                color = Color.Green,
                                fontSize = 12.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        // Кнопка закрытия (в верхнем правом углу списка)
                        IconButton(
                            onClick = { consoleVisible = false },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Green,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 3. Линия-разделитель (опционально, для красоты)
                    HorizontalDivider(color = Color.Green.copy(alpha = 0.3f), thickness = 1.dp)


                    TextField(
                        value = commandText,
                        onValueChange = { commandText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.Green,
                            unfocusedTextColor = Color.Green,
                            cursorColor = Color.Green,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text(" type the command > ", color = Color.DarkGray) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                val cmd = commandText.lowercase().trim()
                                when {
                                    cmd == "win" -> onWinChange(true)
                                    cmd.startsWith("add ") -> {
                                        val amount = cmd.substringAfter("add ").toLongOrNull() ?: 0L
                                        onCountChange(currentCount + amount)
                                        consoleHistory += "\n> Added: $amount"
                                    }
                                    cmd == "cls" -> consoleHistory = "Console was cleared."
                                    cmd == "close" -> consoleVisible = false
                                    else -> consoleHistory += "\n> Error: $cmd"
                                }
                                commandText = ""
                            }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Green)
                            }
                        }
                    )
                }
            }
        }
}


@Composable

fun WinScreen(
    onRestart: () -> Unit,
    globalCount: Long,
    ) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "🎉", fontSize = 100.sp)
        Text(text = "YOU WIN!", fontSize = 40.sp, color = Color.Green)
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "Welcome to The Game.", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 4.dp
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                )
        ) {
            Text(
                text = "Restart Game",
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}