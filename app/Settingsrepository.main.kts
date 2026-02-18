package com.example.pocket20

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val COUNT_KEY = intPreferencesKey("global_count")
        val LEVEL_KEY = intPreferencesKey("shop_level")
        val SPEED_KEY = intPreferencesKey("speed_coef")
    }

    val countFlow: Flow<Int> = context.dataStore.data.map { it[COUNT_KEY] ?: 100 }
    val levelFlow: Flow<Int> = context.dataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val speedFlow: Flow<Int> = context.dataStore.data.map { it[SPEED_KEY] ?: 1 }

    suspend fun saveData(count: Int, level: Int, speed: Int) {
        context.dataStore.edit { preferences ->
            preferences[COUNT_KEY] = count
            preferences[LEVEL_KEY] = level
            preferences[SPEED_KEY] = speed
        }
    }
}