package com.example.pocket20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pocket20.ui.theme.Pocket20Theme


enum class Screen {
    Home, Shop, Profile
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pocket20Theme {
                // --- СОСТОЯНИЕ ПОДНЯТО В MAINACTIVITY ---
                var currentScreen by remember { mutableStateOf(Screen.Home) }
                var globalCount by remember { mutableStateOf(0) }
                var shopLevel by remember { mutableStateOf(0) }
                // ----------------------------------------

                val items = listOf("Главная", "Магазин", "Профиль")
                val icons = listOf(Icons.Filled.Home, Icons.Filled.ShoppingCart, Icons.Filled.Person)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = { Icon(icons[index], contentDescription = item) },
                                    label = { Text(item) },
                                    selected = currentScreen.ordinal == index,
                                    onClick = {
                                        currentScreen = when (index) {
                                            0 -> Screen.Home
                                            1 -> Screen.Shop
                                            2 -> Screen.Profile
                                            else -> Screen.Home
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = Color.LightGray
                    ) {
                        // --- ПЕРЕДАЕМ ЗНАЧЕНИЯ И ФУНКЦИИ ИЗМЕНЕНИЯ ---
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(
                                count = globalCount,
                                onCountChange = { globalCount = it }
                            )
                            Screen.Shop -> ShopScreen(
                                count = globalCount,
                                onCountChange = { globalCount = it },
                                level = shopLevel,
                                onLevelChange = { shopLevel = it }
                            )
                            Screen.Profile -> ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(count: Int, onCountChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Очки: $count", style = MaterialTheme.typography.headlineMedium, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(230.dp))
        Button(
            onClick = { onCountChange(count + 1) }, // Меняем значение через функцию
            modifier = Modifier
                .size(200.dp, 100.dp)
                .padding(8.dp)
        ) {
            Text("Click here", fontSize = 20.sp)
        }
    }
}

@Composable
fun ShopScreen(count: Int, onCountChange: (Int) -> Unit, level: Int, onLevelChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Уровень: $level", style = MaterialTheme.typography.headlineMedium, fontSize = 40.sp)
        Text(text = "Доступно очков: $count", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(150.dp))

        Button (
            onClick = {
                // Логика: если есть 10 очков, тратим их и повышаем уровень
                if (count >= 10) {
                    onCountChange(count - 10)
                    onLevelChange(level + 1)
                }
            },
            modifier = Modifier.size(200.dp, 100.dp)
        ) {
            Text(text = "Upgrade (10)")
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Профиль", style = MaterialTheme.typography.headlineMedium)
    }
}