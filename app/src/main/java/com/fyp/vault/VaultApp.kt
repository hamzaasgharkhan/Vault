package com.fyp.vault

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fyp.vault.data.Node
import com.fyp.vault.data.vaults
import com.fyp.vault.ui.AddNodeType
import com.fyp.vault.ui.AppMode
import com.fyp.vault.ui.AppViewModel
import com.fyp.vault.ui.CreateVaultScreen
import com.fyp.vault.ui.OpenVaultScreen
import com.fyp.vault.ui.Option
import com.fyp.vault.ui.StartScreen
import com.fyp.vault.ui.VaultHomeScreen
import com.fyp.vault.utilities.SystemStatusBarColorChanger

enum class VaultScreen(){
    Start,
    OpenVault,
    CreateVault,
    Vault,
    VaultHomeScreen
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
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = {uris -> appViewModel.setSelectedUris(uris)}
    )

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = {uri ->  if (uri != null) appViewModel.setSelectedUris(listOf(uri))}
    )

    var error = rememberSaveable(appViewModel.appState.value.error) {
        mutableStateOf(appViewModel.appState.value.error)
    }

    /*TODO TEST TO GET ANDROID FILE PICKER*/

    /*TODO TEST END*/

    NavHost(
        navController = navController,
        startDestination = VaultScreen.Start.name
    ){
        composable(
            route = VaultScreen.Start.name
        ){
            appViewModel.clearVaultSelection()
            SystemStatusBarColorChanger(color = MaterialTheme.colorScheme.background)
            StartScreen(
                vaults = vaults,
                onCreateVault = {
                    navController.navigate(VaultScreen.CreateVault.name)
                },
                onVaultClick = {
                    while (!appViewModel.lock){appViewModel.setLock()}
                    appViewModel.selectVault(it)
                    while (appViewModel.lock){/* Wait */}
                    navController.navigate(VaultScreen.OpenVault.name)
                }
            )
        }
        composable(
            route = VaultScreen.CreateVault.name
        ){
            CreateVaultScreen(
                error = error.value,
                navigateUp = {
                    returnToStart(appViewModel, navController)
                },
                onVaultCreate = { name:String, password:String ->
                    /*TODO Update During Backend Addition*/
                    while (!appViewModel.lock){appViewModel.setLock()}
                    Log.d("[VAULT_APP: CREATE_VAULT]", "Lock Value 1: ${appViewModel.lock} ")
                    appViewModel.createVault(name, password)
                    while (appViewModel.lock){/* Wait */}
                    Log.d("[VAULT_APP: CREATE_VAULT]", "Lock Value 1: ${appViewModel.lock} ")
                    if (error.value == null) {
                        Log.d("[VAULT_APP: CREATE_VAULT]", " Apparently No ERRORS ")
                        navController.navigate(VaultScreen.Vault.name) {
                            popUpTo(VaultScreen.CreateVault.name) {
                                inclusive = true
                            }
                        }
                    }
                },
                backHandler = {
                    returnToStart(appViewModel, navController)
                }
            )
        }
        composable(
            route = VaultScreen.OpenVault.name
        ){
            SystemStatusBarColorChanger(color = MaterialTheme.colorScheme.background)
            OpenVaultScreen(
                navigateUp = {
                    returnToStart(appViewModel, navController)
                 },
                inputName = appState.vault,
                onVaultOpen = { name: String, password: String ->
                /*TODO FIX LATER*/
                    while (!appViewModel.lock){appViewModel.setLock()}
                    appViewModel.openVault(name, password)
                    while (appViewModel.lock){/* Wait */}
                    if (error.value == null) {
                        navController.navigate(VaultScreen.Vault.name) {
                            popUpTo(VaultScreen.OpenVault.name) {
                                inclusive = true
                            }
                        }
                    }
                },
                error = error.value,
                backHandler = {
                    returnToStart(appViewModel, navController)
                }
            )
        }
        navigation(
            route = VaultScreen.Vault.name,
            startDestination = VaultScreen.VaultHomeScreen.name
        ){
            composable(
                route = VaultScreen.VaultHomeScreen.name
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
                    onDirectoryClick = { node ->
                        /*TODO this method gets the long index of the folder in the DirectoryStore*/
                        appViewModel.openDirectory(node)
                        navController.navigate(VaultScreen.VaultHomeScreen.name)
                    },
                    onFileClick = { node ->
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
                    selectedOption = appState.selectedOption,
                    showDialog = appState.showDialog,
                    showDialogOption = appState.showDialogOption,
                    selectedNodes = appViewModel.selectedNodes,
                    toggleNodeSelection = { node -> appViewModel.optionHandler(node, Option.Select.name)},
                    closeDialog = {appViewModel.toggleShowDialog(false)},
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
}

private fun returnToStart(
    viewModel: AppViewModel,
    navController: NavController
){
    viewModel.reset()
    navController.popBackStack(VaultScreen.Start.name, false)
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

























