package com.example.pocket20

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

class PreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val GLOBAL_COUNT = longPreferencesKey("global_count")
        val SHOP_LEVEL = intPreferencesKey("shop_level")
        val SPEED_COEF = intPreferencesKey("speed_coef")
        val REBIRTH = intPreferencesKey("rebirth")
        val REBIRTH_COEF = intPreferencesKey("rebirth_coef")
        val COMPLETE_PROFESSIONAL = booleanPreferencesKey("complete_professional")
        val COMPLETE_WAIT = booleanPreferencesKey("complete_wait")
        val COMPLETE_CEO = booleanPreferencesKey("complete_ceo")
    }

    val globalCountFlow: Flow<Long> = context.dataStore.data.map { it[PreferencesKeys.GLOBAL_COUNT] ?: 0L }
    val shopLevelFlow: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.SHOP_LEVEL] ?: 1 }
    val speedCoefFlow: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.SPEED_COEF] ?: 1 }
    val rebirthFlow: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.REBIRTH] ?: 1 }
    val rebirthCoefFlow: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.REBIRTH_COEF] ?: 1 }
    val completeProfessionalFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.COMPLETE_PROFESSIONAL] ?: false }
    val completeWaitFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.COMPLETE_WAIT] ?: false }
    val completeCEOFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.COMPLETE_CEO] ?: false }

    suspend fun saveGlobalCount(count: Long) {
        context.dataStore.edit { it[PreferencesKeys.GLOBAL_COUNT] = count }
    }

    suspend fun saveShopLevel(level: Int) {
        context.dataStore.edit { it[PreferencesKeys.SHOP_LEVEL] = level }
    }

    suspend fun saveSpeedCoef(coef: Int) {
        context.dataStore.edit { it[PreferencesKeys.SPEED_COEF] = coef }
    }

    suspend fun saveRebirth(rebirth: Int) {
        context.dataStore.edit { it[PreferencesKeys.REBIRTH] = rebirth }
    }

    suspend fun saveRebirthCoef(coef: Int) {
        context.dataStore.edit { it[PreferencesKeys.REBIRTH_COEF] = coef }
    }

    suspend fun saveCompleteProfessional(complete: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.COMPLETE_PROFESSIONAL] = complete }
    }

    suspend fun saveCompleteWait(complete: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.COMPLETE_WAIT] = complete }
    }

    suspend fun saveCompleteCEO(complete: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.COMPLETE_CEO] = complete }
    }
}
