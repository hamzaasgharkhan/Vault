package com.fyp.vault.ui.components

import FileSystem.Node
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ViewModule
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fyp.vault.R
import com.fyp.vault.ui.AppMode
import com.fyp.vault.ui.Option
import com.fyp.vault.ui.OptionCategory
import com.fyp.vault.ui.theme.VaultTheme
import java.util.LinkedList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        navigationIcon = {
            if (canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Button")
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultTopBar(
    title: String,
    appMode: String,
    numberOfSelectedNodes: Int,
    navigationAction: () -> Unit,
    pathStack: MutableList<Node>,
    toggleIsListView: () -> Unit,
    onNavigationButtonClick: (Int) -> Unit,
    optionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedOption: String?,
    isListView: Boolean = false
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    when (appMode){
        AppMode.Normal.name -> {
            VaultTopBarNormalMode(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationAction = navigationAction,
                pathStack = pathStack,
                toggleIsListView = toggleIsListView,
                onNavigationButtonClick = onNavigationButtonClick,
                optionClick = optionClick,
                isListView = isListView,
                modifier = modifier
            )
        }
        AppMode.Selection.name -> {
            VaultTopBarSelectionMode(
                numberOfSelectedNodes = numberOfSelectedNodes,
                scrollBehavior = scrollBehavior,
                navigationAction = navigationAction,
                pathStack = pathStack,
                optionClick = optionClick,
                modifier = modifier
            )
        }
        AppMode.TargetPicker.name -> {
            if (selectedOption != null) {
                VaultTopBarTargetPickerMode(
                    selectedOption = selectedOption,
                    numberOfSelectedNodes = numberOfSelectedNodes,
                    scrollBehavior = scrollBehavior,
                    navigationAction = navigationAction,
                    pathStack = pathStack,
                    onNavigationButtonClick = onNavigationButtonClick,
                    optionClick = optionClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultTopBarNormalMode(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationAction: () -> Unit,
    pathStack: MutableList<Node>,
    toggleIsListView: () -> Unit,
    onNavigationButtonClick: (Int) -> Unit,
    optionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isListView: Boolean = false
){
    var showAllOptionsTopBar by rememberSaveable { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
    ){
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ){
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.4f),
                ),
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigationAction) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    if (isListView){
                        // Add Button to Toggle to Grid View
                        IconButton( onClick = toggleIsListView){
                            Icon(
                                imageVector = Icons.Outlined.ViewModule, contentDescription = "View Grid"
                            )
                        }
                    } else {
                        // Add Button to Toggle to List View
                        IconButton( onClick = toggleIsListView){
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = "View List"
                            )
                        }
                    }
                    IconButton( onClick = {showAllOptionsTopBar = true}){
                        Icon(
                            imageVector = Icons.Outlined.MoreVert, contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        /*FIX */
                        expanded = showAllOptionsTopBar,
                        onDismissRequest = {showAllOptionsTopBar = false},
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(
                                start = dimensionResource(id = R.dimen.padding_small),
                                end = dimensionResource(id = R.dimen.padding_small)
                            )
                    ) {
                        OptionCategory.TopBarOptions.options.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }, onClick = {
                                showAllOptionsTopBar = false
                                optionClick(option.name)
                            })
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground, thickness = 1.dp)
                        OptionCategory.VaultOptions.options.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }, onClick = {
                                showAllOptionsTopBar = false
                                optionClick(option.name)
                            })
                        }
                    }
                }
            )
            NavigationBar(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_medium)
                    )
                    .fillMaxWidth(),
                pathStack = pathStack,
                onNavigationButtonClick = onNavigationButtonClick
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultTopBarSelectionMode(
    numberOfSelectedNodes: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationAction: () -> Unit,
    pathStack: MutableList<Node>,
    optionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    var showAllOptionsTopBar by rememberSaveable { mutableStateOf(false) }
    val allNodesAreSelected = if (pathStack.isNotEmpty()){
        pathStack.last().childNodes.size == numberOfSelectedNodes
    } else false
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    ){
        Column(
            modifier = Modifier
        ){
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = {
                    Text(
                        text = "$numberOfSelectedNodes selected",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigationAction) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear Selection")
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton( onClick = {optionClick(Option.Delete.name)} ){
                        Icon(
                            imageVector = Icons.Outlined.Delete, contentDescription = "Delete"
                        )
                    }
                    IconButton( onClick = {optionClick(Option.Export.name)} ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Export"
                        )
                    }
                    IconButton( onClick = {showAllOptionsTopBar = true}){
                        Icon(
                            imageVector = Icons.Outlined.MoreVert, contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        /*FIX */
                        expanded = showAllOptionsTopBar,
                        onDismissRequest = {showAllOptionsTopBar = false},
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(
                                start = dimensionResource(id = R.dimen.padding_small),
                                end = dimensionResource(id = R.dimen.padding_small)
                            )
                    ) {
                        OptionCategory.SelectionModeOptions.options.forEach { option ->
                            if (option == Option.SelectAll){
                                // If All nodes are selected then
                                var validOption: Option = option
                                if (allNodesAreSelected){
                                    validOption = Option.DeSelectAll
                                }
                                DropdownMenuItem(text = {
                                    Text(text = validOption.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }, onClick = {
                                    showAllOptionsTopBar = false
                                    optionClick(validOption.name)
                                })
                                // If only 1 node is selected, display Rename after Select
                                if (numberOfSelectedNodes == 1){
                                    DropdownMenuItem(text = {
                                        Text(text = Option.Rename.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }, onClick = {
                                        showAllOptionsTopBar = false
                                        optionClick(Option.Rename.name)
                                    })
                                }
                            } else {
                                DropdownMenuItem(text = {
                                    Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }, onClick = {
                                    showAllOptionsTopBar = false
                                    optionClick(option.name)
                                })
                            }
                        }
                    }
                }
            )
            NavigationBar(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_medium)
                    )
                    .fillMaxWidth(),
                pathStack = pathStack,
                onNavigationButtonClick =  { _ ->} // Does Nothing
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultTopBarTargetPickerMode(
    selectedOption: String,
    numberOfSelectedNodes: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationAction: () -> Unit,
    onNavigationButtonClick: (Int) -> Unit,
    pathStack: MutableList<Node>,
    optionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    var showAllOptionsTopBar by rememberSaveable { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    ){
        Column(
            modifier = Modifier
        ){
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = {
                    Text(
                        text = when (selectedOption){
                            Option.Copy.name -> {
                               stringResource(id = R.string.copy_title_start) + " $numberOfSelectedNodes item" + if (numberOfSelectedNodes > 1) "s" else ""
                            }
                            Option.Move.name -> {
                                stringResource(id = R.string.move_title_start) + " $numberOfSelectedNodes item" + if (numberOfSelectedNodes > 1) "s" else ""
                            }
                            else -> {/*CODE SHOULD NOT REACH THIS POINT*/""}
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigationAction) { //  THIS NEEDS TO BE FIXED AS RIGHT NOW IT IS NOT CORRECT
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "Cancel Operation")
                    }
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton( onClick = {showAllOptionsTopBar = true}){
                        Icon(
                            imageVector = Icons.Outlined.MoreVert, contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        /*FIX */
                        expanded = showAllOptionsTopBar,
                        onDismissRequest = {showAllOptionsTopBar = false},
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(
                                start = dimensionResource(id = R.dimen.padding_small),
                                end = dimensionResource(id = R.dimen.padding_small)
                            )
                    ) {
                        var selectTargetString: String? = null
                        when (selectedOption){
                            Option.Copy.name -> {
                                selectTargetString = stringResource(id = R.string.select_copy_destination)
                            }
                            Option.Move.name -> {
                                selectTargetString = stringResource(id = R.string.select_move_destination)
                            }
                        }
                        if (selectTargetString != null){
                            DropdownMenuItem(text = {
                                Text(text = selectTargetString, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }, onClick = {
                                showAllOptionsTopBar = false
                                optionClick(Option.SelectTarget.name)
                            })
                        }
                        OptionCategory.TargetPickerModeOptions.options.forEach { option ->
                            DropdownMenuItem(text = {
                                Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }, onClick = {
                                showAllOptionsTopBar = false
                                optionClick(option.name)
                            })
                        }
                    }
                }
            )
            NavigationBar(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_medium)
                    )
                    .fillMaxWidth(),
                pathStack = pathStack,
                onNavigationButtonClick =  onNavigationButtonClick
            )
        }
    }
}





//
//@Preview(
//    showBackground = true,
//    name = "TopBarPreview"
//)
//
//@Composable
//fun AppBarPreview(){
//    VaultTheme {
//        TopBar("Vault", true)
//    }
//}
//
//@Preview(
//    showBackground = true,
//    name = "TopBarPreview LightMode",
//    uiMode = Configuration.UI_MODE_NIGHT_NO
//)
//@Preview(
//    showBackground = true,
//    name = "TopBarPreview DarkMode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun VaultTopBarPreview(){
//    val pathStack = LinkedList<Directory>()
//    pathStack.add(root)
//    pathStack.add(root.childrenDirectories.first())
//    VaultTheme {
//        VaultTopBar(title = "Vault", numberOfSelectedNodes = 0, navigationAction = {}, pathStack = pathStack, isListView = true, toggleIsListView = {}, onNavigationButtonClick = {}, optionClick = {}, appMode = AppMode.Normal.name, selectedOption = null)
//    }
//}


fun adjustBrightness(color: Color, factor: Float): Color {
    // Ensure factor is between 0 and 1
    return color
//    val clampedFactor = (factor / 1000000f).coerceIn(0f, 1f)
//    val min = color.luminance()
//    return color.compositeOver(Color.White)

    // Calculate the new RGB values
//    val red = (color.red + ((1 - color.red) * clampedFactor)).coerceIn(color.colorSpace., 1f)
//    val green = (color.green + ((1 - color.green) * clampedFactor)).coerceIn(0f, 1f)
//    val blue = (color.blue + ((1 - color.blue) * clampedFactor)).coerceIn(0f, 1f)
//
//    return Color(red, green, blue, color.alpha)
}









////////////////////////////////////////////////////// SKELETON ////////////////////////////////////
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun VaultTopBar(
//    title: String,
//    appMode: String,
//    isSelectionViewEnabled: Boolean,
//    numberOfSelectedNodes: Int,
//    canNavigateBack: Boolean,
//    navigateUp: () -> Unit,
//    pathStack: MutableList<Directory>,
//    toggleIsListView: () -> Unit,
//    onNavigationButtonClick: (Int) -> Unit,
//    optionClick: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    isListView: Boolean = false
//){
////    when (appMode){
////        AppMode.Normal.name -> {}
////        AppMode.Selection.name -> {}
////        AppMode.TargetPicker.name -> {}
////    }
//    val topAppBarState = rememberTopAppBarState()
//    var showAllOptionsTopBar by rememberSaveable { mutableStateOf(false) }
//    val allNodesAreSelected = if (pathStack.isNotEmpty()){
//        (pathStack.last().childrenDirectories.size + pathStack.last().childrenFiles.size) == numberOfSelectedNodes
//    } else false
//    Surface(
//        modifier = modifier
//            .background(MaterialTheme.colorScheme.primaryContainer),
//        tonalElevation = 12.dp,
//        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//    ){
//        Column(
//            modifier = Modifier
//        ){
//            TopAppBar(
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
//                ),
//                title = {
//                    when (appMode){
//                        AppMode.Normal.name -> {
//                            Text(
//                                text = title,
//                                style = MaterialTheme.typography.headlineSmall,
//                            )
//                        }
//                        AppMode.Selection.name -> {
//                            Text(
//                                text = "$numberOfSelectedNodes selected",
//                                style = MaterialTheme.typography.titleMedium,
//                            )
//                        }
//                        AppMode.TargetPicker.name -> {}
//                    }
//                },
//                navigationIcon = {
//                    when (appMode){
//                        AppMode.Normal.name -> {
//                            IconButton(onClick = navigateUp) {
//                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Button")
//                            }
//                        }
//                        AppMode.Selection.name -> {
//                            IconButton(onClick = navigateUp) {
//                                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear Selection")
//                            }
//                        }
//                        AppMode.TargetPicker.name -> {}
//                    }
//                },
//                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
//                actions = {
//                    when (appMode){
//                        AppMode.Normal.name -> {
//                            if (isListView){
//                                // Add Button to Toggle to Grid View
//                                IconButton( onClick = toggleIsListView){
//                                    Icon(
//                                        imageVector = Icons.Outlined.ViewModule, contentDescription = "View Grid"
//                                    )
//                                }
//                            } else {
//                                // Add Button to Toggle to List View
//                                IconButton( onClick = toggleIsListView){
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = "View List"
//                                    )
//                                }
//                            }
//                        }
//                        AppMode.Selection.name -> {
//                            IconButton( onClick = {optionClick(Option.Delete.name)} ){
//                                Icon(
//                                    imageVector = Icons.Outlined.Delete, contentDescription = "Delete"
//                                )
//                            }
//                            IconButton( onClick = {optionClick(Option.Export.name)} ){
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Export"
//                                )
//                            }
//                        }
//                        AppMode.TargetPicker.name -> {}
//                    }
//                    IconButton( onClick = {showAllOptionsTopBar = true}){
//                        Icon(
//                            imageVector = Icons.Outlined.MoreVert, contentDescription = "More Options"
//                        )
//                    }
//                    DropdownMenu(
//                        /*FIX */
//                        expanded = showAllOptionsTopBar,
//                        onDismissRequest = {showAllOptionsTopBar = false},
//                        modifier = Modifier
//                            .background(MaterialTheme.colorScheme.surfaceVariant)
//                            .padding(
//                                start = dimensionResource(id = R.dimen.padding_small),
//                                end = dimensionResource(id = R.dimen.padding_small)
//                            )
//                    ) {
//                        if (isSelectionViewEnabled){
//                            OptionCategory.SelectionViewOptions.options.forEach { option ->
//                                if (option == Option.SelectAll){
//                                    // If All nodes are selected then
//                                    var validOption: Option = option
//                                    if (allNodesAreSelected){
//                                        validOption = Option.DeSelectAll
//                                    }
//                                    DropdownMenuItem(text = {
//                                        Text(text = validOption.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                    }, onClick = {
//                                        showAllOptionsTopBar = false
//                                        optionClick(validOption.name)
//                                    })
//                                    // If only 1 node is selected, display Rename after Select
//                                    if (numberOfSelectedNodes == 1){
//                                        DropdownMenuItem(text = {
//                                            Text(text = Option.Rename.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                        }, onClick = {
//                                            showAllOptionsTopBar = false
//                                            optionClick(Option.Rename.name)
//                                        })
//                                    }
//                                } else {
//                                    DropdownMenuItem(text = {
//                                        Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                    }, onClick = {
//                                        showAllOptionsTopBar = false
//                                        optionClick(option.name)
//                                    })
//                                }
//                            }
//                        } else {
//                            OptionCategory.TopBarOptions.options.forEach { option ->
//                                DropdownMenuItem(text = {
//                                    Text(text = option.label, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                }, onClick = {
//                                    showAllOptionsTopBar = false
//                                    optionClick(option.name)
//                                })
//                            }
//                        }
//                    }
//                }
//            )
//            NavigationBar(
//                modifier = Modifier
//                    .padding(
//                        top = dimensionResource(id = R.dimen.padding_medium)
//                    )
//                    .fillMaxWidth(),
//                pathStack = pathStack,
//                onNavigationButtonClick = if (!isSelectionViewEnabled) onNavigationButtonClick else { _ ->}
//            )
//        }
//    }
//    BackHandler {
//        navigateUp()
//    }
//}
