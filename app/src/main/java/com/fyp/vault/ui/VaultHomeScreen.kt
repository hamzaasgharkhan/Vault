package com.fyp.vault.ui

import FileSystem.Node
import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.fyp.vault.R
import com.fyp.vault.ui.components.CircularProgressOverlay
import com.fyp.vault.ui.components.Dialog
import com.fyp.vault.ui.components.VaultContent
import com.fyp.vault.ui.components.VaultTopBar

@SuppressLint("UnrememberedMutableState")
@Composable
fun VaultHomeScreen(
    vault: String,
    pathStack: MutableList<Node>,
    appMode: String,
    selectedOption: String?,
    isListView: Boolean,
    getNodeSize: (Node) -> Long,
    getThumbnail: (Node) -> Bitmap?,
    toggleSelectionMode: (Boolean) -> Unit,
    clearNodeSelection: () -> Unit,
    toggleIsListView: () -> Unit,
    onAddNode: (String) -> Unit,
    onNavigationButtonClick: (Int) -> Unit,
    onDirectoryClick: (Node) -> Unit,
    onFileClick: (Node) -> Unit,
    onTargetSelectClick: () -> Unit,
    backHandler: () -> Unit,
    selectedNodes: MutableList<Node>,
    toggleNodeSelection: (Node) -> Unit,
    showDialog: Boolean,
    showDialogOption: String?,
    dialogSubmitHandler: (Node?, String, String) -> Unit,
    closeDialog: () -> Unit,
    optionClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val loading by rememberSaveable{ mutableStateOf(false) }
    val expandFloatingButtons: MutableState<Boolean> = mutableStateOf(false)
    Scaffold(
        topBar = {
            VaultTopBar(
                title = vault,
                appMode = appMode,
                numberOfSelectedNodes = selectedNodes.size,
                navigationAction = {
                    when (appMode) {
                        AppMode.Normal.name -> {
                            backHandler()
                        }
                        AppMode.Selection.name -> {
                            clearNodeSelection()
                        }
                        AppMode.TargetPicker.name -> { /*TODO FIX*/
                            clearNodeSelection()
                        }
                    }
                },
                pathStack = pathStack,
                isListView = isListView,
                toggleIsListView = toggleIsListView,
                onNavigationButtonClick = onNavigationButtonClick,
                selectedOption = selectedOption,
                optionClick = optionClick
            )
        },
        floatingActionButton = {
            when (appMode){
                AppMode.Normal.name -> {
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))){
                        if (expandFloatingButtons.value){
                            AddNodeType.entries.forEach{ type ->
                                FloatingActionButton(
                                    onClick = {
                                        onAddNode(type.name)
                                        expandFloatingButtons.value = !expandFloatingButtons.value
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = when (type){
                                            AddNodeType.Media -> Icons.Filled.Image
//                                            AddNodeType.Directory -> Icons.Filled.FolderCopy
                                            AddNodeType.File -> Icons.Filled.FileCopy
                                        },
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        contentDescription = stringResource(
                                            when (type){
                                                AddNodeType.Media -> R.string.add_media_button
//                                                AddNodeType.Directory -> R.string.add_directory_button
                                                AddNodeType.File -> R.string.add_file_button
                                            }
                                        )
                                    )
                                }
                            }
                        }
                        FloatingActionButton(
                            onClick = {expandFloatingButtons.value = !expandFloatingButtons.value},
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = stringResource(R.string.add_node_button),
                            )
                        }
                    }
                }
                AppMode.TargetPicker.name -> {
                    FloatingActionButton(
                        onClick = onTargetSelectClick
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.confirm_operation_button)
                        )
                    }
                }
                AppMode.Selection.name -> { /*NO BUTTON NEEDED HERE*/ }
            }
        },
        modifier = modifier
    ) {innerPadding ->
        if (pathStack.isNotEmpty()){
            VaultContent(
                appMode = appMode,
                toggleSelectionMode = toggleSelectionMode,
                selectedNodes = selectedNodes,
                directory = pathStack.last(),
                isListView = isListView,
                getThumbnail = getThumbnail,
                modifier = Modifier.padding(innerPadding),
                onDirectoryClick = onDirectoryClick,
                onFileClick = onFileClick,
                optionClick = optionClick,
                getNodeSize = getNodeSize,
                toggleNodeSelection = toggleNodeSelection
            )
        }
        if (showDialog){
            if (showDialogOption != null) {
                Dialog(
                    selectedOption = showDialogOption,
                    selectedNodes = selectedNodes,
                    onSubmit = dialogSubmitHandler,
                    closeDialog = closeDialog
                )
            }
        }
    }
    if (loading){
        CircularProgressOverlay()
    }
    BackHandler{
        if (!loading){
            when (appMode){
                AppMode.Normal.name -> { backHandler() }
                AppMode.Selection.name -> { clearNodeSelection() }
                AppMode.TargetPicker.name -> { backHandler() }
            }
        }
    }
}

/*
* Folder -> Directory
* Draft -> Node
* */