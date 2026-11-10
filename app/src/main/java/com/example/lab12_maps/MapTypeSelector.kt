package com.example.lab12_maps

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MapType

// Opciones de mapa que se muestran en la barra, con su nombre en español
enum class OpcionMapa(val etiqueta: String, val tipo: MapType) {
    NORMAL("Normal", MapType.NORMAL),
    SATELITE("Satélite", MapType.SATELLITE),
    HIBRIDO("Híbrido", MapType.HYBRID),
    TERRENO("Terreno", MapType.TERRAIN)
}

// Barra flotante con chips para elegir el tipo de mapa
@Composable
fun MapTypeSelector(
    tipoActual: MapType,
    onTipoSeleccionado: (MapType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OpcionMapa.entries.forEach { opcion ->
                FilterChip(
                    selected = tipoActual == opcion.tipo,
                    onClick = { onTipoSeleccionado(opcion.tipo) },
                    label = { Text(opcion.etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}
