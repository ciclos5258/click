package com.example.pocket20

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val COUNT_KEY = longPreferencesKey("global_count")
        val LEVEL_KEY = intPreferencesKey("shop_level")
        val SPEED_KEY = intPreferencesKey("speed_coef")
        val REBIRTH_KEY = intPreferencesKey("rebirth")
        val REBIRTH_COEF_KEY = intPreferencesKey("rebirth_coef")
        val COMP_WASTER_KEY = booleanPreferencesKey("comp_waster")
        val COMP_WAIT_KEY = booleanPreferencesKey("comp_wait")
        val COMP_CEO_KEY = booleanPreferencesKey("comp_ceo")
    }

    val countFlow: Flow<Long> = context.settingsDataStore.data.map { it[COUNT_KEY] ?: 1L }
    val levelFlow: Flow<Int> = context.settingsDataStore.data.map { it[LEVEL_KEY] ?: 1 }
    val speedFlow: Flow<Int> = context.settingsDataStore.data.map { it[SPEED_KEY] ?: 1 }
    val rebirthFlow: Flow<Int> = context.settingsDataStore.data.map { it[REBIRTH_KEY] ?: 1 }
    val rebirthCoefFlow: Flow<Int> = context.settingsDataStore.data.map { it[REBIRTH_COEF_KEY] ?: 1 }
    val compWasterFlow: Flow<Boolean> = context.settingsDataStore.data.map { it[COMP_WASTER_KEY] ?: false }
    val compWaitFlow: Flow<Boolean> = context.settingsDataStore.data.map { it[COMP_WAIT_KEY] ?: false }
    val compCEOFlow: Flow<Boolean> = context.settingsDataStore.data.map { it[COMP_CEO_KEY] ?: false }

    suspend fun saveProgress(
        count: Long,
        level: Int,
        speed: Int,
        rebirth: Int,
        rebirthCoef: Int,
        compWaster: Boolean,
        compWait: Boolean,
        compCEO: Boolean
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[COUNT_KEY] = count
            preferences[LEVEL_KEY] = level
            preferences[SPEED_KEY] = speed
            preferences[REBIRTH_KEY] = rebirth
            preferences[REBIRTH_COEF_KEY] = rebirthCoef
            preferences[COMP_WASTER_KEY] = compWaster
            preferences[COMP_WAIT_KEY] = compWait
            preferences[COMP_CEO_KEY] = compCEO
        }
    }
}
