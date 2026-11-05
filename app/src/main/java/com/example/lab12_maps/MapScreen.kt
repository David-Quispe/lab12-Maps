package com.example.lab12_maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Añadir GoogleMap al layout
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Marcador personalizado en Arequipa, Perú (icono de montaña)
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = bitmapDescriptorFromDrawable(context, R.drawable.ic_mountain),
                title = "Arequipa, Perú"
            )
        }
    }
}
