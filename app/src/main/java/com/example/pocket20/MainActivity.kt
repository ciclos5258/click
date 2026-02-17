package com.example.pocket20

import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pocket20.ui.theme.Pocket20Theme

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
                var globalCount by remember { mutableStateOf(1) }
                var shopLevel by remember { mutableStateOf(1) }
                var speedCoef by remember { mutableStateOf(1) }

                val items = listOf("Shop", "Profile")
                val icons = listOf(Icons.Filled.ShoppingCart, Icons.Filled.Person)

                LaunchedEffect(Unit) {
                    while (true) {
                        val realdelay = 1000L / speedCoef
                        delay(realdelay)
                        globalCount += shopLevel
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
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
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        when (currentScreen) {
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
fun ShopScreen(
    count: Int,
    onCountChange: (Int) -> Unit,
    level: Int,
    onLevelChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Level: $level", style = MaterialTheme.typography.headlineMedium, fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = "Score: $count", fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = "Cost: ${10 * level}", fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(150.dp))

        Button(
            onClick = {
                val currentCost = (10 * level)
                if (count >= currentCost) {
                    onCountChange(count - currentCost)
                    onLevelChange(level + 1)
                }
            },
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.size(200.dp, 100.dp)
        ) {
            Text(text = "Upgrade (${10 * level})", fontSize = 20.sp)
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
        Text(text = "Profile", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}