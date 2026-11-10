package com.example.lab12_maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

// Revisa si el usuario ya dio permiso de ubicación (precisa o aproximada)
fun tienePermisoUbicacion(context: Context): Boolean {
    val fina = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val aproximada = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fina == PackageManager.PERMISSION_GRANTED || aproximada == PackageManager.PERMISSION_GRANTED
}

// Obtiene la ubicación actual con FusedLocationProviderClient.
// Si no llega una lectura nueva, uso la última ubicación conocida.
@SuppressLint("MissingPermission")
fun obtenerUbicacionActual(context: Context, onResultado: (LatLng?) -> Unit) {
    if (!tienePermisoUbicacion(context)) {
        onResultado(null)
        return
    }
    val cliente = LocationServices.getFusedLocationProviderClient(context)
    cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { ubicacion ->
            if (ubicacion != null) {
                onResultado(LatLng(ubicacion.latitude, ubicacion.longitude))
            } else {
                cliente.lastLocation.addOnSuccessListener { ultima ->
                    onResultado(ultima?.let { LatLng(it.latitude, it.longitude) })
                }
            }
        }
        .addOnFailureListener { onResultado(null) }
}
