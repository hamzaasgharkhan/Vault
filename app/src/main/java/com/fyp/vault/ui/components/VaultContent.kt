package com.fyp.vault.ui.components

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
import com.fyp.vault.data.Directory
import com.fyp.vault.data.Node

@Composable
fun VaultContent(
    directory: Directory,
    isListView: Boolean,
    appMode: String,
    toggleSelectionMode: (Boolean) -> Unit,
    selectedNodes: MutableList<Node>,
    optionClick: (String) -> Unit,
    onDirectoryClick: (Node) -> Unit,
    onFileClick: (Node) -> Unit,
    toggleNodeSelection: (Node) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
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
            items = directory.childrenDirectories,
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
                    optionClick = optionClick,
                )
            } else {
                NodeCardGridView(
                    node = node,
                    appMode = appMode,
                    toggleSelectionMode = toggleSelectionMode,
                    isSelected = node in selectedNodes,
                    toggleNodeSelection = toggleNodeSelection,
                    onNodeClick = {onDirectoryClick(node)},
                )
            }
        }
        if (!isListView && directory.childrenDirectories.size % 2 != 0){
            items(1){
            }
        }
        items(
            items =directory.childrenFiles,
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
                    optionClick = optionClick,
                )
            } else {
                NodeCardGridView(
                    node = node,
                    appMode = appMode,
                    toggleSelectionMode = toggleSelectionMode,
                    isSelected = node in selectedNodes,
                    toggleNodeSelection = toggleNodeSelection,
                    onNodeClick = {onFileClick(node)},
                )
            }
        }
    }
}