package com.example.lab12_maps

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

// Convierte un drawable (vector o imagen) en un icono para el marcador
fun bitmapDescriptorFromDrawable(
    context: Context,
    @DrawableRes resId: Int,
    width: Int = 120,
    height: Int = 120
): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, resId)!!
    drawable.setBounds(0, 0, width, height)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    drawable.draw(Canvas(bitmap))
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    // Ejercicio 1: tipo de mapa seleccionado (empieza en Normal)
    var tipoMapa by remember { mutableStateOf(MapType.NORMAL) }

    // Ejercicio 2: permiso y ubicación actual del usuario
    val scope = rememberCoroutineScope()
    var permisoUbicacion by remember { mutableStateOf(tienePermisoUbicacion(context)) }
    var miUbicacion by remember { mutableStateOf<LatLng?>(null) }

    // Pide la ubicación al GPS y mueve la cámara hacia ella
    val irAMiUbicacion = {
        obtenerUbicacionActual(context) { latLng ->
            if (latLng == null) {
                Toast.makeText(context, "No se pudo obtener tu ubicación, revisa que el GPS esté activo", Toast.LENGTH_LONG).show()
            } else {
                miUbicacion = latLng
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                        durationMs = 1500
                    )
                }
            }
        }
    }

    // Lanzador que muestra el diálogo de permisos de Android
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        permisoUbicacion = resultado.values.any { it }
        if (permisoUbicacion) {
            irAMiUbicacion()
        } else {
            Toast.makeText(context, "Sin permiso no puedo mostrar tu ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    // Lista de puntos de interés
    val locations = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994) // Zamacola
    )

    // Polígonos
    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )

    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    // Polilíneas
    // Ruta entre los tres lugares con polígono
    val rutaCentroPolyline = listOf(
        LatLng(-16.399030, -71.536625), // Plaza de Armas
        LatLng(-16.422980, -71.530970), // Parque Lambramani
        LatLng(-16.432660, -71.509240)  // Mall Aventura
    )

    // Recorrido por los puntos de interés
    val recorridoPolyline = listOf(
        LatLng(-16.433415, -71.5442652), // JLByR
        LatLng(-16.4205151, -71.4945209), // Paucarpata
        LatLng(-16.3524187, -71.5675994)  // Zamacola
    )

    // Mover la cámara a Yura con animación de 3 segundos
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f), // Mover a Yura
            durationMs = 3000
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Añadir GoogleMap al layout
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = tipoMapa,
                isMyLocationEnabled = permisoUbicacion // punto azul de Google Maps
            ),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false) // uso mi propio botón
        ) {
            // Marcador en la ubicación actual del usuario
            miUbicacion?.let { posicion ->
                key(posicion) {
                    Marker(
                        state = MarkerState(position = posicion),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        title = "Estoy aquí",
                        snippet = "%.5f, %.5f".format(posicion.latitude, posicion.longitude)
                    )
                }
            }

            // Marcador personalizado en Arequipa, Perú (icono de montaña)
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = bitmapDescriptorFromDrawable(context, R.drawable.ic_mountain),
                title = "Arequipa, Perú"
            )

            // Marcadores de los puntos de interés
            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Ubicación",
                    snippet = "Punto de interés"
                )
            }

            // Polígonos: Plaza de Armas, Parque Lambramani y Mall Aventura
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f),
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f),
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f),
                strokeWidth = 5f
            )

            // Polilínea 1: ruta entre Plaza de Armas, Lambramani y Mall Aventura
            Polyline(
                points = rutaCentroPolyline,
                color = Color.Green,
                width = 8f
            )

            // Polilínea 2: recorrido por JLByR, Paucarpata y Zamacola
            Polyline(
                points = recorridoPolyline,
                color = Color.Magenta,
                width = 6f,
                geodesic = true
            )
        }

        // Selector del tipo de mapa encima del mapa
        MapTypeSelector(
            tipoActual = tipoMapa,
            onTipoSeleccionado = { tipoMapa = it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )

        // Coordenadas actuales y botón "Mi ubicación" en la esquina inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            miUbicacion?.let { posicion ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "Lat: %.5f\nLng: %.5f".format(posicion.latitude, posicion.longitude),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            ExtendedFloatingActionButton(
                onClick = {
                    if (permisoUbicacion) {
                        irAMiUbicacion()
                    } else {
                        permisoLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            ) {
                Text("📍 Mi ubicación")
            }
        }
    }
}
