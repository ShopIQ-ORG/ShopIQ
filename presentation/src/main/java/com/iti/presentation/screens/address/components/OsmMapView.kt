package com.iti.presentation.screens.address.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iti.presentation.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun OsmMapView(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    // Set user agent so OpenStreetMap doesn't block tile requests
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true) // Support pinch-to-zoom
            controller.setZoom(17.0)

            val marker = Marker(this).apply {
                position = GeoPoint(latitude, longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = context.getString(R.string.address_location_detected_title)
            }
            overlays.add(marker)
            controller.setCenter(GeoPoint(latitude, longitude))
        }
    }

    // Update marker position and center map if latitude/longitude change
    LaunchedEffect(latitude, longitude) {
        val geoPoint = GeoPoint(latitude, longitude)
        mapView.controller.setCenter(geoPoint)
        mapView.overlays.filterIsInstance<Marker>().forEach { marker ->
            marker.position = geoPoint
        }
        mapView.invalidate()
    }

    // Update marker styling when primary theme color changes
    LaunchedEffect(primaryColor) {
        val colorInt = primaryColor.toArgb()
        val markerDrawable = createMarkerBitmap(context, colorInt)
        mapView.overlays.filterIsInstance<Marker>().forEach { marker ->
            marker.icon = markerDrawable
        }
        mapView.invalidate()
    }

    // Handle MapView lifecycles using non-deprecated LocalLifecycleOwner
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

private fun createMarkerBitmap(context: Context, color: Int): Drawable {
    val density = context.resources.displayMetrics.density
    val size = (36 * density).toInt()
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Draw Pin Drop Shape
    paint.color = color
    val path = Path()
    val cx = size / 2f
    val cy = size / 3f
    val radius = size / 4f

    path.moveTo(cx, size.toFloat())
    path.cubicTo(
        cx - radius * 1.5f, cy + radius * 1.5f,
        cx - radius, cy - radius,
        cx, cy - radius
    )
    path.cubicTo(
        cx + radius, cy - radius,
        cx + radius * 1.5f, cy + radius * 1.5f,
        cx, size.toFloat()
    )
    path.close()
    canvas.drawPath(path, paint)

    // Draw Inner white circle
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(cx, cy, radius * 0.4f, paint)

    return bitmap.toDrawable(context.resources)
}
