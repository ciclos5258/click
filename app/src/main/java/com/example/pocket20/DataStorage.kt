package com.example.pocket20

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "game_stats")

data class GameData(
    val score: Long,
    val level: Int,
    val speed: Int,
    val rebirth: Int,
    val rebirthCoef: Int,
    val waster: Boolean,
    val wait: Boolean,
    val ceo: Boolean
)

class DataStorage(private val context: Context) {

    companion object {
        val SCORE = longPreferencesKey("score")
        val LEVEL = intPreferencesKey("level")
        val SPEED = intPreferencesKey("speed")
        val REBIRTH = intPreferencesKey("rebirth")
        val REBIRTH_COEF = intPreferencesKey("rebirth_coef")
        val WASTER = booleanPreferencesKey("waster")
        val WAIT = booleanPreferencesKey("wait")
        val CEO = booleanPreferencesKey("ceo")
    }

    suspend fun saveGame(
        score: Long,
        level: Int,
        speed: Int,
        reb: Int,
        rebC: Int,
        waster: Boolean,
        wait: Boolean,
        ceo: Boolean
    ) {
        context.dataStore.edit { pref ->
            pref[SCORE] = score
            pref[LEVEL] = level
            pref[SPEED] = speed
            pref[REBIRTH] = reb
            pref[REBIRTH_COEF] = rebC
            pref[WASTER] = waster
            pref[WAIT] = wait
            pref[CEO] = ceo
        }
    }

    val gameDataFlow: Flow<GameData> = context.dataStore.data.map { pref ->
        GameData(
            score = pref[SCORE] ?: 0L,
            level = pref[LEVEL] ?: 1,
            speed = pref[SPEED] ?: 1,
            rebirth = pref[REBIRTH] ?: 1,
            rebirthCoef = pref[REBIRTH_COEF] ?: 1,
            waster = pref[WASTER] ?: false,
            wait = pref[WAIT] ?: false,
            ceo = pref[CEO] ?: false
        )
    }

    val scoreFlow: Flow<Long> = context.dataStore.data.map { it[SCORE] ?: 0L }
    val levelFlow: Flow<Int> = context.dataStore.data.map { it[LEVEL] ?: 1 }
    val speedFlow: Flow<Int> = context.dataStore.data.map { it[SPEED] ?: 1 }
    val rebirthFlow: Flow<Int> = context.dataStore.data.map { it[REBIRTH] ?: 1 }
    val rCoefFlow: Flow<Int> = context.dataStore.data.map { it[REBIRTH_COEF] ?: 1 }
}
