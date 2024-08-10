package com.fyp.vault.ui.components

import FileSystem.Node
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fyp.vault.R
import com.fyp.vault.ui.AppMode
import com.fyp.vault.ui.Option
import com.fyp.vault.ui.OptionCategory
import com.fyp.vault.utilities.calculateSizeInStringFormat
import com.fyp.vault.utilities.isNodeVideo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NodeCardListView(
    node: Node,
    appMode: String,
    thumbnail: Bitmap?,
    onNodeClick: () -> Unit,
    optionClick: (String) -> Unit,
    toggleNodeSelection: (Node) -> Unit,
    toggleSelectionMode: (Boolean) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
){
    var showAllOptionsNode by rememberSaveable{mutableStateOf(false)}
    val haptics = LocalHapticFeedback.current
    Card(
        colors = if (appMode == AppMode.Selection.name && isSelected)  {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) }
        ,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (appMode == AppMode.Selection.name){
                        toggleNodeSelection(node)
                    } else {
                        onNodeClick()
                    }
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    toggleSelectionMode(true)
                    toggleNodeSelection(node)
                },
                onLongClickLabel = null
            )

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small))
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .align(Alignment.CenterStart)
            ){
                if (!node.isDirectory && thumbnail != null){
                    // File with thumbnail
                    Box(modifier = Modifier.size(dimensionResource(id = R.dimen.list_thumbnail_size))){
                        Image(
                            bitmap = thumbnail.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(dimensionResource(id = R.dimen.list_thumbnail_size))
                                .clip(MaterialTheme.shapes.medium)
                        )
                        if (isNodeVideo(node)){
                            Icon(
                                imageVector = Icons.Filled.PlayCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .scale(0.5f)
                            )
                        }
                    }
                } else {
                    // Either a directory or a file without thumbnail
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.list_thumbnail_size))
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = MaterialTheme.shapes.medium
                            )
                    ){
                        Icon(
                            imageVector = if (node.isDirectory) Icons.Filled.FolderOpen else Icons.AutoMirrored.Outlined.InsertDriveFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .clip(MaterialTheme.shapes.medium)
                                .align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.list_view_thumbnail_separator)))
                Text(
                    text = node.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            when (appMode){
                AppMode.Normal.name -> {
                    IconButton( onClick = {showAllOptionsNode = true}, modifier = Modifier.align(Alignment.CenterEnd)){
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More Options",
                        )
                    }
                    Box(modifier = Modifier.align(Alignment.BottomEnd)){
                        DropdownMenu(
                            expanded = showAllOptionsNode,
                            onDismissRequest = {showAllOptionsNode = false},
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .align(Alignment.BottomEnd)
                        ) {
                            DropdownMenuItem(text = {
                                Text(
                                    text = Option.Select.label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }, onClick = {
                                showAllOptionsNode = false
                                toggleNodeSelection(node)
                                toggleSelectionMode(true)
                            })
                            OptionCategory.NodeOptions.options.forEach { option ->
                                DropdownMenuItem(text = {
                                    Text(
                                        text = option.label,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }, onClick = {
                                    showAllOptionsNode = false
                                    toggleNodeSelection(node)
                                    optionClick(option.name)
                                })
                            }
                        }
                    }
                }
                AppMode.Selection.name -> {
                    IconButton( onClick = {toggleNodeSelection(node)}, modifier = Modifier.align(Alignment.CenterEnd)){
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            contentDescription = "Select",
                        )
                    }
                }
                AppMode.TargetPicker.name -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NodeCardGridView(
    node: Node,
    size: Long,
    thumbnail: Bitmap?,
    onNodeClick: () -> Unit,
    appMode: String,
    toggleNodeSelection: (Node) -> Unit,
    toggleSelectionMode: (Boolean) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val isDirectory = rememberSaveable() {
        node.isDirectory
    }
    val haptics = LocalHapticFeedback.current
    val sizeInString: String? = if (!isDirectory) calculateSizeInStringFormat(size) else null
    Box(
        modifier = if (!isDirectory && isSelected) {
            modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F),
                )
                .padding(16.dp)
        } else modifier
    ){
        OutlinedCard(
            colors = if (appMode == AppMode.Selection.name && isSelected)  {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) }
            ,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                     if (appMode == AppMode.Selection.name){
                         toggleNodeSelection(node)
                     } else {
                         onNodeClick()
                     }
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        toggleSelectionMode(true)
                        toggleNodeSelection(node)
                    },
                    onLongClickLabel = null
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isDirectory) dimensionResource(id = R.dimen.padding_small) else 0.dp)
            ) {
                if (isDirectory){
                    // Directory
                    if (appMode == AppMode.Selection.name){
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                                .clip(MaterialTheme.shapes.medium),
                        )
                    }
                    Text(text = node.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                } else {
                    // File
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface
                            )
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        ){
                            if (thumbnail != null) {
                                // Thumbnail exists
                                Image(
                                    bitmap = thumbnail.asImageBitmap(),
                                    contentDescription = node.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .align(Alignment.Center)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .scale(2f)
                                        .clip(MaterialTheme.shapes.medium)
                                        .align(Alignment.Center),
                                )
                            }
                        }
                        if (!isSelected){
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(
                                        top = dimensionResource(id = R.dimen.padding_medium),
                                        end = dimensionResource(id = R.dimen.padding_small)
                                    )
                            ) {
                                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                                    Text(
                                        text = sizeInString!!,
                                        style = MaterialTheme.typography.titleSmall.copy(shadow = Shadow(
                                            color = Color.Black, offset = Offset(0f, 0f), blurRadius = 2f
                                        )),
                                        color = Color.White,
                                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                                    )
                                    if (isNodeVideo(node)){
                                        Icon(
                                            imageVector = Icons.Filled.PlayCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier
                                                .padding(
                                                    end = dimensionResource(id = R.dimen.padding_small)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                        if (appMode == AppMode.Selection.name){
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = "Select",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(dimensionResource(id = R.dimen.padding_medium))
                                    .background(
                                        color = if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                            if (isSelected && isNodeVideo(node)){
                                Icon(
                                    imageVector = Icons.Filled.PlayCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            top = dimensionResource(id = R.dimen.padding_medium),
                                            end = dimensionResource(id = R.dimen.padding_small)
                                        )
                                )
                            }
                        }
                        if (thumbnail == null){
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        bottom = dimensionResource(id = R.dimen.padding_medium),
                                        start = dimensionResource(id = R.dimen.padding_small),
                                        end = dimensionResource(id = R.dimen.padding_small)
                                    )
                            ){
                                Text(
                                    text = node.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleSmall.copy(shadow = Shadow(
                                        color = Color.Black, offset = Offset(3.0f, 0f), blurRadius = 3f
                                    )),
                                    color = Color.White,
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}