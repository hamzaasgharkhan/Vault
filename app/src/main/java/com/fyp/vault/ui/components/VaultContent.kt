package com.fyp.vault.ui.components

import FileSystem.Node
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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