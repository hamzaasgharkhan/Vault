package com.fyp.vault

import FileSystem.Node
import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fyp.vault.ui.AddNodeType
import com.fyp.vault.ui.AppMode
import com.fyp.vault.ui.AppViewModel
import com.fyp.vault.ui.CreateVaultScreen
import com.fyp.vault.ui.OpenVaultScreen
import com.fyp.vault.ui.Option
import com.fyp.vault.ui.StartScreen
import com.fyp.vault.ui.VaultHomeScreen
import com.fyp.vault.ui.components.IndefiniteCircularProgressIndicator
import com.fyp.vault.utilities.SystemStatusBarColorChanger

enum class Route(){
    Start,
    OpenVault,
    CreateVault,
    Vault
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VaultApp(
    appViewModel: AppViewModel,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
){
    /*
    * TRY
    * Create a navigation field in the viewmodel.
    * use LaunchedEffect or something like that to navigate to the correct page whenever the
    * navigation changes within the viewmodel
    *
    * */
    /*TODO Add UIState*/
    val appState by appViewModel.appState.collectAsState()

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris ->
            appViewModel.setSelectedUris(uris)
            appViewModel.handleFileSelection()
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = {uris ->
            appViewModel.setSelectedUris(uris)
            appViewModel.handleFileSelection()
        }

    )

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = {uri ->
            if (uri != null){
                appViewModel.setSelectedUris(listOf(uri))
                appViewModel.handleDirectorySelection()
            }
        }
    )

    val error = rememberSaveable(appViewModel.appState.value.error) {
        mutableStateOf(appViewModel.appState.value.error)
    }

    LaunchedEffect(appState.route) {
        when (appState.route){
            Route.Start.name -> {
                navController.popBackStack(Route.Start.name, false)
            }
            Route.CreateVault.name -> {
                navController.navigate(Route.CreateVault.name)
            }
            Route.OpenVault.name -> {
                navController.navigate(Route.OpenVault.name)
            }
            Route.Vault.name -> {
                navController.navigate(Route.Vault.name) {
                    popUpTo(Route.Start.name) {
                        inclusive = false
                    }
                }
            }
        }
    }

    /*TODO TEST TO GET ANDROID FILE PICKER*/

    /*TODO TEST END*/
    NavHost(
        navController = navController,
        startDestination = Route.Start.name
    ){
        composable(
            route = Route.Start.name
        ){
            SystemStatusBarColorChanger(color = MaterialTheme.colorScheme.background)
            StartScreen(
                vaults = appViewModel.vaults, // Replace with appViewModel.vaults
                onCreateVault = {
                    appViewModel.navigateTo(Route.CreateVault.name)
                },
                onVaultClick = {
                    Log.d("[VAULT_APP: ON_VAULT_CLICK]", "VaultName: $it")
                    appViewModel.selectVault(it)
                }
            )
        }
        composable(
            route = Route.CreateVault.name
        ){
            CreateVaultScreen(
                error = error.value,
                navigateUp = {
                    returnToStart(appViewModel, navController)
                },
                onVaultCreate = { name:String, password:String ->
                    /*TODO Update During Backend Addition*/
                    appViewModel.createVault(name, password)
                },
                backHandler = {
                    returnToStart(appViewModel, navController)
                }
            )
        }
        composable(
            route = Route.OpenVault.name
        ){
            SystemStatusBarColorChanger(color = MaterialTheme.colorScheme.background)
            OpenVaultScreen(
                navigateUp = {
                    returnToStart(appViewModel, navController)
                 },
                inputName = appState.vault,
                onVaultOpen = { name: String, password: String ->
                /*TODO FIX LATER*/
                    appViewModel.openVault(name, password)
                },
                error = error.value,
                backHandler = {
                    returnToStart(appViewModel, navController)
                }
            )
        }
        composable(
            route = Route.Vault.name
        ){
            SystemStatusBarColorChanger(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.1f))
            VaultHomeScreen(
                vault = appState.vault,
                isListView = appState.isListView,
                appMode = appState.appMode,
                toggleSelectionMode = { value: Boolean ->
                    appViewModel.toggleSelectionMode(value)
                },
                clearNodeSelection = {appViewModel.resetNodeSelection()},
                toggleIsListView = {appViewModel.toggleIsListView()},
                onAddNode = { option ->
                    /*TODO Currently no implementation is in place for this method.*/
                    when (option){
                        AddNodeType.Directory.name -> {
                            directoryPicker.launch(null)
                        }
                        AddNodeType.File.name -> {
                            filePicker.launch(
                                arrayOf("*/*")
                            )
                        }
                        AddNodeType.Media.name -> {
                            mediaPicker.launch(PickVisualMediaRequest())
                        }
                    }
                },
                onNavigationButtonClick = { pathStackIndex ->
                    /*TODO the method gets the integer index of the clicked navigation item within the LinkedList. Delete all the elements after that.*/
                    val elementsRemoved = appViewModel.navigateInPathStack(pathStackIndex)
                    if (elementsRemoved != 0){
                        for (i in 1..elementsRemoved){
                            navController.popBackStack()
                        }
                    }
                },
                onDirectoryClick = { node: Node ->
                    /*TODO this method gets the long index of the folder in the DirectoryStore*/
                    appViewModel.openDirectory(node)
                    navController.navigate(Route.Vault.name)
                },
                onFileClick = { node: Node ->
                    /*TODO this method gets the long index of the file within the DirectoryStore*/
                    appViewModel.openFile(node)
                },
                pathStack = appViewModel.pathStack,
                optionClick = { selectedOption ->
                    Log.d("[VAULT_APP: OPTION_CLICK]","Option: $selectedOption")
                    when (selectedOption){
                        Option.CreateDirectory.name,
                        Option.Delete.name -> {
                            appViewModel.setShowDialogOption(selectedOption)
                            appViewModel.toggleShowDialog(true)
                        }
                        Option.Rename.name -> {
                            if (appViewModel.selectedNodes.size == 1){
                                appViewModel.setShowDialogOption(selectedOption)
                                appViewModel.toggleShowDialog(true)
                            }
                        }
                        Option.SelectAll.name,
                        Option.DeSelectAll.name -> {
                            appViewModel.optionHandler(null, selectedOption)
                        }
                        Option.Copy.name,
                        Option.Move.name -> {
                            appViewModel.setSelectedOption(selectedOption)
                            appViewModel.setAppMode(AppMode.TargetPicker.name)
                            /*TODO
                            *  Set the selected option through the viewModel. Create a method there
                            *  Change the app mode to TargetPicker Mode.
                            *
                            * OTHER
                            * Fix cancel button
                            * */
                        }
                        Option.Cancel.name -> {
                            appViewModel.resetNodeSelection()
                        }
                        Option.SelectTarget.name -> {
                            appViewModel.targetSelected()
                        }
                        Option.Export.name -> {

                        }
                    }
                },
                getNodeSize = {node -> appViewModel.getSize(node)},
                selectedOption = appState.selectedOption,
                showDialog = appState.showDialog,
                showDialogOption = appState.showDialogOption,
                selectedNodes = appViewModel.selectedNodes,
                toggleNodeSelection = { node: Node -> appViewModel.optionHandler(node, Option.Select.name)},
                closeDialog = {appViewModel.toggleShowDialog(false)},
                getThumbnail = {node -> appViewModel.getThumbnail(node)},
                dialogSubmitHandler = { node: Node?, option:String, value: String ->
                    when (option){
                        Option.CreateDirectory.name -> {
                            appViewModel.optionHandler(null, option, value)
                        }
                        Option.Rename.name -> {
                            appViewModel.optionHandler(node, option, value)
                        }
                        Option.Delete.name -> {
                            appViewModel.optionHandler(node, option)
                        }
                        Option.ExitVault.name -> {
                            appViewModel.exitVault()
                            returnToStart(appViewModel, navController)
                        }
                    }
                },
                onTargetSelectClick = {
                    appViewModel.targetSelected()
                },
                backHandler = {
                    val exitingVault:Boolean = appViewModel.backHandler()
                    if (exitingVault){
                        appViewModel.setShowDialogOption(Option.ExitVault.name)
                        appViewModel.toggleShowDialog(true)
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

private fun returnToStart(
    viewModel: AppViewModel,
    navController: NavController
){
    viewModel.reset()
    navController.popBackStack(Route.Start.name, false)
}



//
//@Preview
//@Composable
//fun VaultAppPreview(){
//    VaultTheme {
//        Scaffold(){innerPadding ->
//            VaultApp(modifier = Modifier.padding(innerPadding))
//        }
//    }
//}



/*TODO
*       Move
*       Copy
*       Add Functionality to the Add Node button to go to the document selection view to select stuff
*       Export, get the target destination
*       Once the above is complete, work on storage permissions and stuff, place an image on the phone
*           and load that image through an InputStream both as a thumbnail and open it as an actual
*           file.
*       Create a mechanism to handle input errors
*       Create a mechanism for loading screen
*       <<<< UI COMPLETE >>>>
*       Work on backend, finish missing stuff like thumbnail and combine backend to frontend.
*
*
*       UI ADDITIONAL
*       Add a confirmation before exiting a vault through its backhandler
*
*
*
*
*
*
*
*
*
*
*
*
*
*
* */

























