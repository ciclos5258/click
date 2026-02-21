package com.example.pocket20

import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import com.example.pocket20.ui.theme.Pocket20Theme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    Shop, Profile
}

class GameViewModel(private val dataStorage: DataStorage) : ViewModel() {
    private val _state = MutableStateFlow<GameData?>(null)
    val state: StateFlow<GameData?> = _state.asStateFlow()

    private val _isWon = MutableStateFlow(false)
    val isWon: StateFlow<Boolean> = _isWon.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.Shop)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val winScore = 10_000_000L

    init {
        viewModelScope.launch {
            dataStorage.gameDataFlow.collect { savedData ->
                if (_state.value == null) {
                    _state.value = savedData
                }
            }
        }

        // Game Loop
        viewModelScope.launch {
            while (true) {
                val currentState = _state.value
                if (currentState != null && !_isWon.value) {
                    val safeSpeed = if (currentState.speed <= 0) 1 else currentState.speed
                    delay(1000L / safeSpeed)

                    val newScore = currentState.score + (currentState.level.toLong() * currentState.rebirthCoef * currentState.rebirth)

                    _state.update { it?.copy(
                        score = newScore,
                        waster = it.waster || it.level > 99,
                        wait = it.wait || it.speed > 99,
                        ceo = it.ceo || it.level > 999
                    ) }

                    if (newScore >= winScore) {
                        _isWon.value = true
                    }
                } else {
                    delay(1000L)
                }
            }
        }

        // Periodic Save (every 10 seconds)
        viewModelScope.launch {
            while (true) {
                delay(10000L)
                saveToStorage()
            }
        }
    }

    private suspend fun saveToStorage() {
        _state.value?.let { s ->
            dataStorage.saveGame(s.score, s.level, s.speed, s.rebirth, s.rebirthCoef, s.waster, s.wait, s.ceo)
        }
    }

    fun setScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    fun upgradeLevel() {
        _state.value?.let { currentState ->
            val cost = (10 * currentState.level).toLong()
            if (currentState.score >= cost) {
                _state.update { it?.copy(
                    score = it.score - cost,
                    level = it.level + 1
                ) }
                viewModelScope.launch { saveToStorage() }
            }
        }
    }

    fun upgradeSpeed() {
        _state.value?.let { currentState ->
            val cost = (100 * currentState.speed).toLong()
            if (currentState.score >= cost) {
                _state.update { it?.copy(
                    score = it.score - cost,
                    speed = it.speed + 1
                ) }
                viewModelScope.launch { saveToStorage() }
            }
        }
    }

    fun addScore(amount: Long) {
        _state.update { it?.copy(score = it.score + amount) }
    }

    fun setWon(won: Boolean) {
        _isWon.value = won
    }

    fun restartGame() {
        _state.update { it?.copy(
            score = 0L,
            level = 1,
            speed = 1,
            rebirth = it.rebirth + 1,
            rebirthCoef = it.rebirthCoef + 5
        ) }
        _isWon.value = false
        _currentScreen.value = Screen.Shop
        viewModelScope.launch { saveToStorage() }
    }
}

class GameViewModelFactory(private val dataStorage: DataStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(dataStorage) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStorage = DataStorage(this)

        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel by viewModels { GameViewModelFactory(dataStorage) }
            Pocket20Theme {
                val state by viewModel.state.collectAsState()
                val isWon by viewModel.isWon.collectAsState()
                val currentScreen by viewModel.currentScreen.collectAsState()

                if (state != null) {
                    val items = listOf("Shop", "Profile")
                    val icons = listOf(Icons.Filled.Home, Icons.Filled.Person)

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (!isWon) {
                                Column {
                                    HorizontalDivider(color = Color.Black, thickness = 2.dp)
                                    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                                        items.forEachIndexed { index, item ->
                                            NavigationBarItem(
                                                icon = { Icon(icons[index], contentDescription = item) },
                                                label = { Text(item) },
                                                selected = currentScreen.ordinal == index,
                                                onClick = {
                                                    viewModel.setScreen(if (index == 0) Screen.Shop else Screen.Profile)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            if (isWon) {
                                WinScreen(
                                    rebirth = state!!.rebirth,
                                    onRestart = { viewModel.restartGame() }
                                )
                            } else {
                                when (currentScreen) {
                                    Screen.Shop -> ShopScreen(
                                        count = state!!.score,
                                        level = state!!.level,
                                        speedCoef = state!!.speed,
                                        onUpgradeLevel = { viewModel.upgradeLevel() },
                                        onUpgradeSpeed = { viewModel.upgradeSpeed() }
                                    )
                                    Screen.Profile -> ProfileScreen(
                                        gameData = state!!,
                                        onAddScore = { viewModel.addScore(it) },
                                        onWinChange = { viewModel.setWon(it) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ShopScreen(
    count: Long,
    level: Int,
    speedCoef: Int,
    onUpgradeLevel: () -> Unit,
    onUpgradeSpeed: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.images),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextInsideProgressBar(
                progress = (count.toFloat() / 10000000f).coerceIn(0f, 1f),
                modifier = Modifier.weight(5f),
                height = 70.dp
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "Level: $level", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Speed Coef: $speedCoef", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Score: $count", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onUpgradeLevel,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(220.dp, 80.dp)
        ) {
            Text(text = "Upgrade Lvl\n(${10 * level})", textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onUpgradeSpeed,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(220.dp, 80.dp)
        ) {
            Text(text = "SpeedUp\n(${100 * speedCoef})", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ProfileScreen(
    gameData: GameData,
    onAddScore: (Long) -> Unit,
    onWinChange: (Boolean) -> Unit
) {
    var clickCount by remember { mutableStateOf(0) }
    var consoleVisible by remember { mutableStateOf(false) }
    var consoleHistory by remember { mutableStateOf("Console initialized...") }
    var commandText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            AchievementItem("The End?", "Get 10,000,000 score.", gameData.rebirth > 1) {
                clickCount++
                if (clickCount > 4) consoleVisible = true
            }
            AchievementItem("Here we go again...", "Get 10,000,000 score again.", gameData.rebirth > 2)
            AchievementItem("Professional waster", "Reach 100 level.", gameData.waster)
            AchievementItem("Wait! That's Illegal!", "Reach 100 speed.", gameData.wait)
            AchievementItem("CEO of AFK", "Reach 1000 level.", gameData.ceo)
            AchievementItem("10/10", "Gues :)", gameData.rebirth > 10)
        }

        if (consoleVisible) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(300.dp).padding(16.dp),
                border = BorderStroke(1.dp, Color.Green)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Dev Console", color = Color.Green, fontSize = 12.sp)
                        IconButton(onClick = { consoleVisible = false }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color.Green)
                        }
                    }
                    Text(consoleHistory, color = Color.Green, fontSize = 10.sp, modifier = Modifier.weight(1f).verticalScroll(scrollState))
                    TextField(
                        value = commandText,
                        onValueChange = { commandText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.Green, unfocusedTextColor = Color.Green, cursorColor = Color.Green),
                        trailingIcon = {
                            IconButton(onClick = {
                                val cmd = commandText.lowercase().trim()
                                when {
                                    cmd == "win" -> onWinChange(true)
                                    cmd.startsWith("add ") -> {
                                        val amount = cmd.substringAfter("add ").toLongOrNull() ?: 0L
                                        onAddScore(amount)
                                        consoleHistory += "\n> Added $amount"
                                    }
                                    cmd == "cls" -> consoleHistory = "Cleared."
                                    else -> consoleHistory += "\n> Unknown: $cmd"
                                }
                                commandText = ""
                            }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Green) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementItem(title: String, desc: String, isUnlocked: Boolean, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = if (isUnlocked) Color(0xFFC8E6C9) else Color.LightGray,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, fontSize = 14.sp)
        }
    }
}

@Composable
fun WinScreen(rebirth: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 YOU WIN! 🎉", fontSize = 36.sp, color = Color.Green, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        val message = when {
            rebirth == 1 -> "Wait... there's more?"
            rebirth < 5 -> "Getting faster every time!"
            rebirth < 10 -> "You are a true clicker master."
            else -> "Legendary status achieved!"
        }
        Text(message, fontSize = 24.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Restart (Rebirth +$rebirth)", color = Color.Black, fontSize = 20.sp)
        }
    }
}

@Composable
fun TextInsideProgressBar(progress: Float, modifier: Modifier = Modifier, height: Dp = 30.dp) {
    Box(
        modifier = modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(height / 2)).background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (progress > 0f) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color(0xFF4CAF50)).align(Alignment.CenterStart))
        }
        Text("Progress: ${(progress * 100).toInt()}%", color = Color.Black, fontWeight = FontWeight.Bold)
    }
}