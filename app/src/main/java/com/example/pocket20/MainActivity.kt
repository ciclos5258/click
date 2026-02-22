package com.example.pocket20

import kotlinx.coroutines.delay
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

enum class Screen {
    Shop, Profile
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefsManager = remember { PreferencesManager(context) }
            
            Pocket20Theme {
                var currentScreen by remember { mutableStateOf(Screen.Shop) }
                
                val savedGlobalCount by prefsManager.globalCountFlow.collectAsState(initial = 0L)
                val savedShopLevel by prefsManager.shopLevelFlow.collectAsState(initial = 1)
                val savedSpeedCoef by prefsManager.speedCoefFlow.collectAsState(initial = 1)
                val savedRebirth by prefsManager.rebirthFlow.collectAsState(initial = 1)
                val savedRebirthCoef by prefsManager.rebirthCoefFlow.collectAsState(initial = 1)
                val savedCompleteProfessional by prefsManager.completeProfessionalFlow.collectAsState(initial = false)
                val savedCompleteWait by prefsManager.completeWaitFlow.collectAsState(initial = false)
                val savedCompleteCEO by prefsManager.completeCEOFlow.collectAsState(initial = false)

                var globalCount by remember { mutableStateOf(0L) }
                var shopLevel by remember { mutableStateOf(1) }
                var speedCoef by remember { mutableStateOf(1) }
                var isWon by remember { mutableStateOf(false) }
                var rebirth by remember { mutableStateOf(1) }
                var rebirthCoef by remember { mutableStateOf(1) }
                var completeProfessionaltimewaster by remember { mutableStateOf(false) }
                var completeWait by remember { mutableStateOf(false) }
                var completeCEO by remember { mutableStateOf(false) }

                val scope = rememberCoroutineScope()

                LaunchedEffect(savedGlobalCount) { globalCount = savedGlobalCount }
                LaunchedEffect(savedShopLevel) { shopLevel = savedShopLevel }
                LaunchedEffect(savedSpeedCoef) { speedCoef = savedSpeedCoef }
                LaunchedEffect(savedRebirth) { rebirth = savedRebirth }
                LaunchedEffect(savedRebirthCoef) { rebirthCoef = savedRebirthCoef }
                LaunchedEffect(savedCompleteProfessional) { completeProfessionaltimewaster = savedCompleteProfessional }
                LaunchedEffect(savedCompleteWait) { completeWait = savedCompleteWait }
                LaunchedEffect(savedCompleteCEO) { completeCEO = savedCompleteCEO }

                val items = listOf("Shop", "Profile")
                val icons = listOf(Icons.Filled.Home, Icons.Filled.Person)
                val winScore = 1_000_000L

                LaunchedEffect(speedCoef, isWon) {
                    while (!isWon) {
                        val safeSpeed = if (speedCoef <= 0) 1 else speedCoef
                        val realDelay = 1000L / safeSpeed
                        delay(realDelay)
                        globalCount += shopLevel * rebirthCoef * rebirth
                        
                        if (shopLevel > 99 && !completeProfessionaltimewaster) {
                            completeProfessionaltimewaster = true
                            prefsManager.saveCompleteProfessional(true)
                        }
                        if (speedCoef > 99 && !completeWait) {
                            completeWait = true
                            prefsManager.saveCompleteWait(true)
                        }
                        if (shopLevel > 999 && !completeCEO) {
                            completeCEO = true
                            prefsManager.saveCompleteCEO(true)
                        }
                        
                        if (globalCount >= winScore) {
                            isWon = true
                        }
                        prefsManager.saveGlobalCount(globalCount)
                    }
                }

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
                                rebirth = rebirth,
                                onRestart = {
                                    scope.launch {
                                        globalCount = 0L
                                        shopLevel = 1
                                        speedCoef = 1
                                        rebirth++
                                        rebirthCoef += 5
                                        isWon = false
                                        currentScreen = Screen.Shop
                                        
                                        prefsManager.saveGlobalCount(globalCount)
                                        prefsManager.saveShopLevel(shopLevel)
                                        prefsManager.saveSpeedCoef(speedCoef)
                                        prefsManager.saveRebirth(rebirth)
                                        prefsManager.saveRebirthCoef(rebirthCoef)
                                    }
                                })
                        } else {
                            when (currentScreen) {
                                Screen.Shop -> ShopScreen(
                                    count = globalCount,
                                    onCountChange = { 
                                        globalCount = it 
                                        scope.launch { prefsManager.saveGlobalCount(it) }
                                    },
                                    level = shopLevel,
                                    onLevelChange = { 
                                        shopLevel = it 
                                        scope.launch { prefsManager.saveShopLevel(it) }
                                    },
                                    speedCoef = speedCoef,
                                    onSpeedChange = { 
                                        speedCoef = it 
                                        scope.launch { prefsManager.saveSpeedCoef(it) }
                                    }
                                )
                                Screen.Profile -> ProfileScreen(
                                    currentCount = globalCount,
                                    onCountChange = { 
                                        globalCount = it 
                                        scope.launch { prefsManager.saveGlobalCount(it) }
                                    },
                                    onWinChange = { isWon = it },
                                    rebirth = rebirth,
                                    shopLevel = shopLevel,
                                    speedCoef = speedCoef,
                                    completeProfessionaltimewaster = completeProfessionaltimewaster,
                                    completeWait = completeWait,
                                    completeCEO = completeCEO
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
fun TextInsideProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 30.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(com.example.pocket20.ui.theme.ProgressBarColor)
                    .align(Alignment.CenterStart)
            )
        }

        Text(
            text = "Completed: ${(progress * 100).toInt()}%",
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 16.sp
        )
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
    )
    {
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
                progress = (count.toFloat() / 1000000f).coerceIn(0f, 1f), 
                modifier = Modifier.weight(5f),
                height = 70.dp
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        Text(text = "Level: $level", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Speed Coef: $speedCoef", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Score: $count", fontSize = 40.sp)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                val currentCost = (10 * level).toLong()
                if (count >= currentCost) {
                    onCountChange(count - currentCost)
                    onLevelChange(level + 1)
                }
            },
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            ),
            modifier = Modifier
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
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            ),
            modifier = Modifier
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
    rebirth: Int,
    shopLevel: Int,
    speedCoef: Int,
    completeProfessionaltimewaster: Boolean,
    completeWait: Boolean,
    completeCEO: Boolean
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
                Text(text =  "The End?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Get 1,000,000 score.", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (rebirth > 2) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "Here we go again...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Get 1,000,000 score over.", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (completeProfessionaltimewaster) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "Professional time waster", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Reach 100 level.", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (completeWait) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "Wait! That's Illegal!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Reach 100 speed coef.", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (completeCEO) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "CEO of AFK", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Reach 1000 level.", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = if (rebirth > 10) Color.Green else Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text =  "10/10", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Gues :)", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
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
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                ) {
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
    globalCount: Long,
    rebirth: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(text = "🎉YOU WIN!🎉", fontSize = 30.sp, color = Color.Green)
        Spacer(modifier = Modifier.height(30.dp))
        when (rebirth) {
            1 -> {
                Text(text = "Or not?", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Welcome to The Game.", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            2 -> {
                Text(text = "Here we go again?", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            3 -> {
                Text(text = "Successfully Unproductive", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            4 -> {
                Text(text = "You’re Doing Great, Sweetie", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            5 -> {
                Text(text = "You are awsome!", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            6 -> {
                Text(text = "You a professional life waster!", fontSize = 30.sp, modifier = Modifier.padding(32.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            7 -> {
                Text(text = "Stop it! You are doing this in vain", fontSize = 30.sp, modifier = Modifier.padding(32.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            8 -> {
                Text(text = "Alright, you win. Happy now?", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            9 -> {
                Text(text = "You’re close to...", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            10 -> {
                Text(text = "You’re actually doing it!", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
            else -> {
                Text(text = "", fontSize = 30.sp)
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

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
