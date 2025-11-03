package com.llucs.motioncues

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensão para criar o DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFS_NAME)

class SettingsDataStore(private val context: Context) {

    private val ACTIVATION_MODE_KEY = stringPreferencesKey(Constants.KEY_ACTIVATION_MODE)
    private val DOT_COLOR_KEY = intPreferencesKey(Constants.KEY_DOT_COLOR)
    private val DOT_COUNT_KEY = intPreferencesKey(Constants.KEY_DOT_COUNT)
    private val DOT_SIZE_KEY = intPreferencesKey(Constants.KEY_DOT_SIZE)
    private val EFFECT_ACTIVE_KEY = stringPreferencesKey(Constants.KEY_EFFECT_ACTIVE) // Usando String para simular Boolean, pois DataStore não tem BooleanKey

    // Flow para ler o modo de ativação
    val activationModeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ACTIVATION_MODE_KEY] ?: Constants.DEFAULT_ACTIVATION_MODE
        }

    // Flow para ler a cor das bolinhas
    val dotColorFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DOT_COLOR_KEY] ?: Constants.DEFAULT_DOT_COLOR
        }

    // Flow para ler a quantidade de bolinhas
    val dotCountFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DOT_COUNT_KEY] ?: Constants.DEFAULT_DOT_COUNT
        }

    // Flow para ler o tamanho das bolinhas
    val dotSizeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DOT_SIZE_KEY] ?: Constants.DEFAULT_DOT_SIZE
        }

    // Flow para ler o estado do efeito (ativo/inativo)
    val effectActiveFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            (preferences[EFFECT_ACTIVE_KEY] ?: "false").toBoolean()
        }

    // Salvar o modo de ativação
    suspend fun saveActivationMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVATION_MODE_KEY] = mode
        }
    }

    // Salvar a cor das bolinhas
    suspend fun saveDotColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[DOT_COLOR_KEY] = color
        }
    }

    // Salvar a quantidade de bolinhas
    suspend fun saveDotCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[DOT_COUNT_KEY] = count
        }
    }

    // Salvar o tamanho das bolinhas
    suspend fun saveDotSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[DOT_SIZE_KEY] = size
        }
    }

    // Salvar o estado do efeito (ativo/inativo)
    suspend fun saveEffectActive(isActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[EFFECT_ACTIVE_KEY] = isActive.toString()
        }
    }
}
