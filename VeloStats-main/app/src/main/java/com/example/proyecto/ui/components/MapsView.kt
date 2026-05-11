package com.example.proyecto.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Tasks
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import com.example.proyecto.ui.ViewModels.RideViewModel
import androidx.preference.PreferenceManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.LocationUtils.getLastKnownLocation


@Composable
fun MapsView(modifier: Modifier = Modifier, viewModel: RideViewModel) {
    val context = LocalContext.current
    val rideState by viewModel.rideState.collectAsState()
    val useLightSensor by viewModel.useLightSensor.collectAsState()
    // Estado para la ubicación actual
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }


    // Inicializa OSMDroid
    Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

    // Configurar FusedLocationProviderClient para actualizaciones de ubicación
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.create().apply {
        interval = 1000L // Actualizar cada segundo
        fastestInterval = 500L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Configurar listener para actualizaciones de ubicación
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                Log.d("MapsView", "Nueva ubicación recibida: ${geoPoint.latitude}, ${geoPoint.longitude}")
                currentLocation = geoPoint
                viewModel.addLocationToRoute(location)
            }
        }
    }

    // Manejo del ciclo de vida del MapView
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(mapViewRef.value) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapViewRef.value?.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapViewRef.value?.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    mapViewRef.value?.onDetach()
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }


    // Iniciar y detener actualizaciones de ubicación con DisposableEffect
    DisposableEffect(Unit) {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            Log.e("MapsView", "Permisos de ubicación no concedidos", e)
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Crear el MapView
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                mapViewRef.value = this
                // Estilo de mapa según el sensor
                tileProvider.clearTileCache()
                if (useLightSensor) {
                    // Modo oscuro
                    setTileSource(
                        XYTileSource(
                            "CartoDarkMatter",
                            0,
                            19,
                            256,
                            ".png",
                            arrayOf("https://basemaps.cartocdn.com/dark_all/")
                        )
                    )
                } else {
                    // Modo claro por defecto
                    setTileSource(TileSourceFactory.MAPNIK)
                }

                // Configuración del mapa
                controller.setZoom(15.0)
                val fallbackLocation = GeoPoint(4.6285, -74.0647)  // Ubicación inicial (ejemplo Bogotá)
                controller.setCenter(fallbackLocation)

                // Marcador inicial
                val marker = Marker(this)
                marker.position = fallbackLocation
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Ubicación inicial"
                overlays.add(marker)
            }
        }
        ,
        update = { mapView ->
            // Limpiar las Polyline existentes (excepto el marcador)
            mapView.overlays.removeAll { it is Polyline }

            // Dibujar segmentos de la ruta con colores basados en la velocidad
            val routePoints = rideState.routePoints
            val newTileSource = if (useLightSensor) {
                XYTileSource(
                    "CartoDarkMatter",
                    0, 19, 256, ".png",
                    arrayOf("https://basemaps.cartocdn.com/dark_all/")
                )
            } else {
                TileSourceFactory.MAPNIK
            }

            // Verificar si el TileSource actual es diferente al nuevo
            if (mapView.tileProvider.tileSource.name() != newTileSource.name()) {
                mapView.tileProvider.clearTileCache()
                mapView.setTileSource(newTileSource)
                mapView.invalidate()
            }
            if (routePoints.size >= 2) {
                for (i in 0 until routePoints.size - 1) {
                    val startPoint = routePoints[i]
                    val endPoint = routePoints[i + 1]

                    // Calcular la velocidad promedio entre los dos puntos
                    val speed = (startPoint.speedKmH + endPoint.speedKmH) / 2

                    // Determinar el color del segmento según la velocidad
                    val lineColor = when {
                        speed < 10f -> android.graphics.Color.GREEN // Baja velocidad: Verde
                        speed < 30f -> android.graphics.Color.YELLOW // Velocidad moderada: Amarillo
                        else -> android.graphics.Color.RED // Alta velocidad: Rojo
                    }

                    // Crear una Polyline para este segmento
                    val segment = Polyline(mapView)
                    segment.setColor(lineColor)
                    segment.setWidth(5f)
                    segment.setPoints(listOf(startPoint.toGeoPoint(), endPoint.toGeoPoint()))
                    mapView.overlays.add(segment)
                }
            }

            // Actualizar el marcador con la ubicación actual
            val marker = mapView.overlays.find { it is Marker } as? Marker
            val locationToUse = currentLocation ?: rideState.currentLocation
            if (locationToUse != null) {
                marker?.position = locationToUse
                marker?.title = "Mi ubicación"
                mapView.controller.setCenter(locationToUse)
            }

            // Forzar redibujado del mapa
            mapView.invalidate()
        }
    )


    LaunchedEffect(useLightSensor) {
        if (useLightSensor) {
            LightSensorController.start(context) { lux ->
                mapViewRef.value?.let { cambiarEstiloMapaPorLuz(it, lux) }
            }
        } else {
            LightSensorController.stop()
        }
    }


}

private fun cambiarEstiloMapaPorLuz(mapView: MapView, lux: Float) {
    val tileSource = if (lux < 50) {
        // Modo noche (usar un tile oscuro si tienes uno, ejemplo: TOPO)
        TileSourceFactory.USGS_TOPO
    } else {
        // Modo normal
        TileSourceFactory.MAPNIK
    }

    if (mapView.tileProvider.tileSource.name() != tileSource.name()) {
        mapView.setTileSource(tileSource)
    }
}

object LightSensorController {
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var listener: SensorEventListener? = null

    fun start(context: Context, onLuxChanged: (Float) -> Unit) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val lux = event?.values?.get(0) ?: return
                onLuxChanged(lux)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        lightSensor?.let {
            sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(listener)
    }


}





@SuppressLint("MissingPermission")
private fun getLastKnownLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        val locationTask = fusedLocationClient.lastLocation
        Tasks.await(locationTask)
    } catch (e: Exception) {
        null
    }
}