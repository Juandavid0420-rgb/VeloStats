package com.example.proyecto.ui.ViewModels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Definir DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsViewModel : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (_themeMode.value == ThemeMode.AUTO) {
                val lux = event.values[0]
                _themeMode.value = if (lux < 50) ThemeMode.DARK else ThemeMode.LIGHT
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
    }

    // Función para inicializar las preferencias con un Context
    fun initializePreferences(context: Context) {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                preferences[THEME_MODE_KEY]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.AUTO
            }.collect { mode ->
                _themeMode.value = mode
            }
            context.dataStore.data.map { preferences ->
                preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
            }.collect { enabled ->
                _notificationsEnabled.value = enabled
            }
        }
    }

    fun setThemeMode(mode: ThemeMode, context: Context? = null) {
        _themeMode.value = mode
        if (context != null) {
            viewModelScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[THEME_MODE_KEY] = mode.name
                }
            }
        }
        if (mode == ThemeMode.AUTO && context != null) {
            registerLightSensor(context)
        } else {
            unregisterLightSensor()
        }
    }

    fun toggleNotifications(context: Context? = null) {
        _notificationsEnabled.value = !_notificationsEnabled.value
        if (context != null) {
            viewModelScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[NOTIFICATIONS_ENABLED_KEY] = _notificationsEnabled.value
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Limpiar tokens o datos de autenticación (puedes usar DataStore aquí si es necesario)
            println("Cerrar sesión ejecutado.")
        }
    }

    private fun registerLightSensor(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            // Fallback: Usar tema claro si no hay sensor
            _themeMode.value = ThemeMode.LIGHT
            return
        }
        sensorManager?.registerListener(
            sensorListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun unregisterLightSensor() {
        sensorManager?.unregisterListener(sensorListener)
        sensorManager = null
        lightSensor = null
    }

    override fun onCleared() {
        unregisterLightSensor()
        super.onCleared()
    }
}

enum class ThemeMode {
    LIGHT, DARK, AUTO
}