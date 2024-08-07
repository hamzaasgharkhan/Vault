package com.fyp.vault.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun IndefiniteCircularProgressIndicator(modifier: Modifier = Modifier){
    val infiniteTransition = rememberInfiniteTransition(label = "infinite_loop")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // Duration of one loop
                easing = LinearEasing // Smooth and continuous animation
            ),
            repeatMode = RepeatMode.Restart // Restart animation when it reaches the end
        ), label = "infinite_loop"
    )
    CircularProgressIndicator(
        progress = { progress },
        modifier = modifier
            .width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun CircularProgressOverlay(modifier: Modifier = Modifier){
    val interactionSource = remember { MutableInteractionSource()}
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
            .clickable(interactionSource = interactionSource, indication = null){}
            .zIndex(100f)
    ) {
        IndefiniteCircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}