package com.fyp.vault.ui.components

import FileSystem.Node
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.fyp.vault.R

@Composable
fun VaultContent(
    directory: Node,
    isListView: Boolean,
    appMode: String,
    getNodeSize: (Node) -> Long,
    getThumbnail: (Node) -> Bitmap?,
    toggleSelectionMode: (Boolean) -> Unit,
    selectedNodes: MutableList<Node>,
    optionClick: (String) -> Unit,
    onDirectoryClick: (Node) -> Unit,
    onFileClick: (Node) -> Unit,
    toggleNodeSelection: (Node) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val directories = directory.childNodes.filter{node -> node.isDirectory}
    val files = directory.childNodes.filter{node -> !node.isDirectory}
    if (directory.childNodes.size == 0){
        Box(modifier = Modifier.fillMaxSize()){
            Text(
                text = stringResource(id = R.string.directory_empty),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Center),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(
                count = if (isListView) 1 else 2
            ),
            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            state = lazyGridState,
            modifier = modifier
        ) {
            items(
                items = directories,
                key = { node -> node.index }
            ) { node ->
                if (isListView) {
                    NodeCardListView(
                        node = node,
                        onNodeClick = {onDirectoryClick(node)},
                        appMode = appMode,
                        toggleNodeSelection = toggleNodeSelection,
                        toggleSelectionMode = toggleSelectionMode,
                        isSelected = node in selectedNodes,
                        thumbnail = null,   // since it is a directory
                        optionClick = optionClick,
                    )
                } else {
                    NodeCardGridView(
                        node = node,
                        appMode = appMode,
                        size = getNodeSize(node),
                        toggleSelectionMode = toggleSelectionMode,
                        isSelected = node in selectedNodes,
                        toggleNodeSelection = toggleNodeSelection,
                        thumbnail = null,   // since it is a directory
                        onNodeClick = {onDirectoryClick(node)},
                    )
                }
            }
            if (!isListView && directories.size % 2 != 0){
                items(1){
                }
            }
            items(
                items = files,
                key = { node -> node.index }
            ) { node ->
                if (isListView) {
                    NodeCardListView(
                        node = node,
                        appMode = appMode,
                        toggleSelectionMode = toggleSelectionMode,
                        isSelected = node in selectedNodes,
                        onNodeClick = {onFileClick(node)},
                        toggleNodeSelection = toggleNodeSelection,
                        thumbnail = getThumbnail(node),
                        optionClick = optionClick,
                    )
                } else {
                    NodeCardGridView(
                        node = node,
                        appMode = appMode,
                        size = getNodeSize(node),
                        toggleSelectionMode = toggleSelectionMode,
                        isSelected = node in selectedNodes,
                        toggleNodeSelection = toggleNodeSelection,
                        thumbnail = getThumbnail(node),
                        onNodeClick = {onFileClick(node)},
                    )
                }
            }
        }
    }
}