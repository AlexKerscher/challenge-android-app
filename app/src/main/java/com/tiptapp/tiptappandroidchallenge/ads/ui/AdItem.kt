package com.tiptapp.tiptappandroidchallenge.ads.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tiptapp.tiptappandroidchallenge.R

@Composable
fun AdItem(
    displayAd: DisplayAd,
    isSelected: Boolean,
    onSelectionChanged: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "AdItemBackground"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onSelectionChanged() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)) {

            AsyncImage(
                model = displayAd.ad.thumbnail,
                contentDescription = displayAd.ad.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            // Selection indicator border
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, color = backgroundColor)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = displayAd.ad.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconRes = when (displayAd.ad.type) {
                        1L -> R.drawable.recycling_24dp
                        2L -> R.drawable.featured_seasonal_and_gifts_24dp
                        3L -> R.drawable.shopping_cart_24dp
                        4L -> R.drawable.directions_car_24dp
                        else -> null
                    }
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Ad type",
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Text(
                        text = "${displayAd.ad.pay} ${displayAd.ad.ccy}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.weight(1f))

                    displayAd.distanceInKm?.let { distance ->
                        Text(
                            text = "%.1f km".format(distance),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}