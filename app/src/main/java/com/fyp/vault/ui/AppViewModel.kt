package com.fyp.vault.ui

import FileSystem.FileSystem
import FileSystem.InputFile
import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Size
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.fyp.vault.Route
import com.fyp.vault.data.Directory
import com.fyp.vault.data.Node
import com.fyp.vault.data.root
import com.fyp.vault.utilities.createThumbnailAsStream
import com.fyp.vault.utilities.getInputStreamFromBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.InputStream

class AppViewModel(private val app: Application) : AndroidViewModel(app) {
    val vaults: MutableList<String> = mutableListOf()

    // Android FILES
    private var selectedUris: List<Uri> = emptyList()
        private set

    fun setSelectedUris(uris: List<Uri>){
        selectedUris = uris
        /*TODO Delete the code that follows*/
    }

    private lateinit var fileSystem: FileSystem;

    /**
     * Returns a list of the names of all available vaults
     */

    fun initVaults(){
        vaults.clear()
        val baseDirectory = app.filesDir
        val directories = baseDirectory.listFiles { file -> file.isDirectory }
        for (directory in directories){
            val superBlock = File(directory, "super-block")
            if (superBlock.isFile){
                vaults.add(directory.name)
            }
        }
    }

    fun handleFileSelection(){
            for (uri in selectedUris){
                addFile(uri)
            }
//            uri.encodedPath
//            Log.d("[INTERNAL_VIEW_MODEL: SET_SELECTED_URI]", "Uri Scheme: ${uri.scheme} Path: ${uri.path} isAbsolute: ${uri.isAbsolute}")
//            val inputStream= app.contentResolver.openInputStream(uri)
//            val byteArray = ByteArray(4096)
//            while (
//                inputStream!!.read(byteArray) != -1
//            ) {}
//            inputStream.close()
//              }
    }

    fun handleDirectorySelection(){
        for (uri in selectedUris){
            addDirectory(uri)
        }
    }

    // App State
    private val _appState = MutableStateFlow(AppState())
    var appState: StateFlow<AppState> = _appState.asStateFlow()

    // File System Data
    val pathStack: MutableList<Directory> = mutableStateListOf()
    val selectedNodes: MutableList<Node> = mutableStateListOf()
    // TO MIMIC THE FILESYSTEM
    private var indexValue: Long = 50


////////////////////////////////////////////////////////////////////////////////////////////////////
/////////   Vault Access (External)
////////////////////////////////////////////////////////////////////////////////////////////////////

    fun navigateTo(route:String){
        _appState.update{currentValue ->
            currentValue.copy(route = route)
        }
    }

    fun setError(error: Error?){
        _appState.update{currentValue ->
            currentValue.copy(error = error)
        }
    }

    fun clearError(){
        setError(null)
    }

    fun setVaultName(name: String){
        _appState.update{currentValue ->
            currentValue.copy(vault = name)
        }
        Log.d("[INTERNAL_VIEW_MODEL: SET_VAULT_NAME]", "Name: ${appState.value.vault}")
    }


    fun selectVault(name: String){
        _appState.update{currentValue ->
            currentValue.copy(vault = name, route = Route.OpenVault.name)
        }
    }

    fun clearVaultSelection(){
        setVaultName("")
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
/////////   Vault Control Operations (Creation, Deletion, Mounting, Unmounting)
////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createVault(name: String, password: String){
        if (vaults.contains(name)){
            setError(Error.CreateVault_InvalidName_AlreadyExists)
            Log.d("[INTERNAL_VIEW_MODE: CREATE_VAULT]", "error: ${appState.value.error}")
        } else if (name.isEmpty()){
            setError(Error.CreateVault_InvalidName_Blank)
            Log.d("[INTERNAL_VIEW_MODE: CREATE_VAULT]", "error: ${appState.value.error}")
        } else if (password.isEmpty()){
            setError(Error.CreateVault_InvalidPassword_Blank)
            Log.d("[INTERNAL_VIEW_MODE: CREATE_VAULT]", "error: ${appState.value.error}")
        } else {
            // NO Errors, File System Can be Created
            Log.d("[INTERNAL_VIEW_MODE: CREATE_VAULT]", "HERE! error: ${appState.value.error}")
            _createVault(name, password)
            navigateTo(Route.Vault.name)
        }
    }

    private fun _createVault(name: String, password: String){
        /*TODO Remove the dummy code*/
        vaults.add(name)
        try {
            fileSystem = FileSystem.createFileSystem(app.filesDir, name, password)
        } catch (e: Exception){
            /*TODO Throw Error Here in the UI*/
        }
        _appState.value = AppState(vault = name)
        pathStack.add(_getRoot())
        fileSystem.createDirectory("/", "sheep")
        fileSystem.createDirectory("/", "dark")
        fileSystem.printTree()
        /*TODO Connect to the FileSystem CreateFileSystem Method*/

    }

    fun openVault(name: String, password: String){
        /*TODO Implement Later*/
        if (!vaults.contains(name)){
            setError(Error.OpenVault_InvalidName_DoesNotExist)
        } else if (name.isEmpty()){
            setError(Error.OpenVault_InvalidName_Blank)
        } else if (password.isEmpty()){
            setError(Error.OpenVault_InvalidPassword_Blank)
        } else {
//            TODO("This Place will handle the actual opening of the vault.")

        }
        _openVault(name, password)
        navigateTo(Route.Vault.name)
    }

    fun _openVault(name: String, password:String){
        _appState.value = AppState(vault = name)
        pathStack.add(_getRoot())
    }

    private fun _updateState(vault: String, path: String){
        _appState.update{ currentState ->
            currentState.copy(vault = vault)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
/////////   Vault Internal Operations
////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getThumbnail(uri: Uri): InputStream?{
        Log.d("THUMBNAIL", "${app.contentResolver.getType(uri)} ${uri.encodedAuthority}")
//        val type = uri.lastPathSegment?.split(":")?.get(0) ?: return null
        val type = app.contentResolver.getType(uri)?.split("/")?.get(0)
        when (type){
            "video" -> {
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(app.applicationContext, uri)
                val bitmap = metadataRetriever.getFrameAtIndex(0) ?: return null
                return createThumbnailAsStream(bitmap, 300)
            }
            "image" -> {
                var thumbnail: InputStream? = null
                app.contentResolver.openInputStream(uri).use {stream ->
                    if (stream != null) {
                        thumbnail =  createThumbnailAsStream(stream, 300)
                    }
                }
                return thumbnail
            }
            "audio" -> {
                return null
            }
            else -> {
                return null
            }
        }
    }

    private fun getInputFileFromUri(uri: Uri): InputFile{
        Log.d("INPUT_FILE: Authority", "${uri.authority}")
        Log.d("VAULTS", "$vaults")

        var name: String = ""
        var parentPath: String = pathStack.last().path // FIX: CHANGE IT TO THE ACTUAL NODE getPath Method
        var size: Long = -1
        var lastModifiedTime: Long = 0  /*TODO Fix this to get the actual value*/
        var creationTime: Long = 0  /*TODO Fix this to get the actual value*/
        var fileInputStream: InputStream?
        var thumbnailInputStream: InputStream? = null
        val projection = arrayOf(
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )
        app.contentResolver.query(uri, projection, null, null, null)?.use{ cursor ->
            if (cursor.moveToFirst()){
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val lastModifiedIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                name = cursor.getString(nameIndex)
                size = cursor.getLong(sizeIndex)
                lastModifiedTime = cursor.getLong(lastModifiedIndex)
                creationTime = lastModifiedTime
            }
        }
        fileInputStream = app.contentResolver.openInputStream(uri)
        if (fileInputStream == null) {
            throw Exception("FileInputStream is null")
        }
        if (name == "" || size == -1L){
            throw Exception("Details were not loaded")
        }
        thumbnailInputStream = getThumbnail(uri)
        Log.d("INPUT_FILE", "Name: $name | Size: $size | fileinputstream: $fileInputStream | thumbnail: $thumbnailInputStream")
        return InputFile(name, parentPath, size, creationTime, lastModifiedTime, fileInputStream, thumbnailInputStream)
    }

    fun addFile(uri: Uri){
        // Get a Uri. Make it in a format accessible for the FileSystem.
        // Ensure that the target exists in the file system, if not create it.
        // Add the file to the filesystem.
        val file = getInputFileFromUri(uri)
        Log.d("ADD_FILE", "Name: ${file.name} | ParentPath: ${file.parentPath} | Size: ${file.size}")
        /*TODO DO STUFF
        *  Call the filesystem method to add the file to the filesystem.
        * */
        file.close()
//        val file = File(URI(uri.toString()))
//        Log.d("ADD_FILE", "File: ${file.name}")
    }


    fun addDirectory(uri: Uri){
        // Get a Uri.
        // Create a directory for it in the filesystem if it doesn't exist.
        // Recursively add all the folders and files within the Uri to the filesystem.
    }

    fun createDirectory(){
        TODO("Create this method to create a directory in the filesystem")
    }



    fun openDirectory(node: Node){
        /*TODO Implement using the filesystem's methods*/
        pathStack.add(node as Directory)
    }

    fun copyNode(node: Node, targetDestination: Node){
        Log.d("[INTERNAL_VIEW_MODEL: COPY_NODE]", "Node: ${node.name} | TargetDestination: ${targetDestination.name}")
    }

    fun moveNode(node: Node, targetDestination: Node){
        Log.d("[INTERNAL_VIEW_MODEL: MOVE_NODE]", "Node: ${node.name} | TargetDestination: ${targetDestination.name}")
    }

    fun openFile(node: Node){

    }

    /**
     * Removes all the elements after the provided index. The element at the given index becomes the
     * last element in the LinkedList
     * @param index The index of the desired last element in the pathStack.
     * @return The number of elements removed.
     */
    fun navigateInPathStack(index: Int): Int{    // index within the LinkedList. Returns the
        val lastIndex = pathStack.size - 1
        if (index == lastIndex)
            return 0
        for (i in index+1 .. lastIndex){
            pathStack.removeLast()
        }
        return lastIndex - index
    }

    /**
     * Goes back within the Vault.
     * @return true if and only if the user exists the vault after the back operation, false
     * otherwise
     */
    fun backHandler(): Boolean{
        if (pathStack.size < 2){
            Log.d("INTERNAL_VIEW_MODEL: BACK_HANDLER", "EXIT VAULT")
            return true
        }
        else{
            pathStack.removeLast()
            return false
        }
    }

    fun topBarOptionHandler(option: String, value: String){
        if (option == Option.CreateDirectory.name){
            Log.d("[INTERNAL_VIEW_MODEL: TOP_BAR_OPTION_HANDLER]", "Option Called: CreateDirectory")
            indexValue++
            /*TODO REPLACE THIS WITH THE ACTUAL FILE SYSTEM CALL*/
            pathStack.last().childrenDirectories.add(
                Directory(
                    name = value,
                    childrenDirectories = mutableListOf(),
                    childrenFiles = mutableListOf(),
                    index = indexValue
                )
            )
        }
    }

    fun setShowDialogOption(option: String?){
        _appState.update{ currentValue ->
            currentValue.copy(showDialogOption = option)
        }
    }

    fun toggleShowDialog(value: Boolean? = null){
        var newValue: Boolean? = value
        if (newValue == null){
            newValue = !_appState.value.showDialog
        }
        if (!newValue){
            // If new Value is false, the dialog box will no longer be displayed.
            // Set the showDialogOption to null
            setShowDialogOption(null)
            // If only one item was selected for the dialog, clear selections.
            if (selectedNodes.size == 1){
                resetNodeSelection()
            }
        }
        _appState.update{ currentValue ->
            currentValue.copy(showDialog = newValue)
        }
    }

    fun _getRoot(): Directory{
        /*TODO Replace with logic to get real root from the actual file system*/
        return root
    }

    fun toggleIsListView(){
        _appState.update{ currentValue ->
            currentValue.copy(isListView = !_appState.value.isListView)
        }
    }

    private fun createFolder(name: String){
        Log.d("[INTERNAL_VIEW_MODEL: TOP_BAR_OPTION_HANDLER]", "Option Called: CreateDirectory")
        indexValue++
        /*TODO REPLACE THIS WITH THE ACTUAL FILE SYSTEM CALL*/
        pathStack.last().childrenDirectories.add(
            Directory(
                name = name,
                childrenDirectories = mutableListOf(),
                childrenFiles = mutableListOf(),
                index = indexValue
            )
        )
    }

    private fun renameNode(node: Node, name: String){
        Log.d("[INTERNAL_VIEW_MODEL: RENAME_NODE]", name)
        // IT DOES REACH THIS POINT
        // RENAME DOESN'T HAPPEN FOR SOME REASON
        node.name = name
    }

    private fun deleteNode(node: Node){
        // Delete the Given Node
        Log.d("[INTERNAL_VIEW_MODEL: DELETE_NODE]", node.name)
    }

    fun addNodeToSelection(node: Node){
        if (node !in selectedNodes){
            selectedNodes.add(node)
        }
    }

    fun removeNodeFromSelection(node: Node){
        selectedNodes.remove(node)
    }

    fun toggleSelectionMode(value: Boolean? = null){
        var newValue: Boolean? = value
        if (newValue == null){
            newValue = appState.value.appMode != AppMode.Selection.name // if already selection mode, new value should be false else true
        }
        _appState.update{currentValue ->
            currentValue.copy(appMode = if (newValue) AppMode.Selection.name else AppMode.Normal.name)
        }
    }

    /**
     * @param node The target of the operation. Null indicates that more than one nodes have been
     *      selected and the operation needs to be performed on the bulk of them.
     * @param option Desired operation to be performed on the node(s)
     * @param value Contains any potential information that may be required to carry out the operation.
     *
     */
    fun optionHandler(node: Node?, option: String, value: String? = null) {
        Log.d("[INTERNAL_VIEW_MODEL: NODE_OPTION_HANDLER]", "Option $option Node $node LinkedListSize ${selectedNodes.size}" )
        when (option){
            Option.Select.name -> {
                if (node != null){
                    if (node !in selectedNodes){
                        addNodeToSelection(node)
                    } else {
                        removeNodeFromSelection(node)
                    }
                }
                // If no nodes are selected after the select operation, disable SelectionView if it was enabled
                if (selectedNodes.size == 0){
                    toggleSelectionMode(false)
                }
            }
            Option.CreateDirectory.name -> {
                if (value != null){
                    createFolder(value)
                }
            }
            Option.Rename.name -> {
                if (node != null && value != null){
                    renameNode(node, value)
                }
            }
            Option.Delete.name -> {
                selectedNodes.forEach { selectedNode ->
                    deleteNode(selectedNode)
                }
            }
            Option.Copy.name -> {
                /*TODO Implement Copy*/
            }
            Option.Move.name -> {
                /*TODO Implement Move*/
            }
            Option.SelectAll.name -> {
                if (appState.value.appMode != AppMode.Selection.name){
                    toggleSelectionMode(true)
                }
                selectAllNodes()
            }
            Option.DeSelectAll.name -> {
                // Do Nothing As ResetNodeSelection will happen anyways
            }
        }
        if (option != Option.Select.name && option != Option.SelectAll.name && option != Option.CreateDirectory.name){
            resetNodeSelection()
        }
    }

    private fun selectAllNodes(){
        val path: Directory? = pathStack.lastOrNull()
        if (path != null){
            path.childrenDirectories.forEach { node -> addNodeToSelection(node)}
            path.childrenFiles.forEach { node -> addNodeToSelection(node)}
        }
    }

    fun setAppMode(mode: String){
        _appState.update {currentValue ->
            currentValue.copy(appMode = mode)
        }
    }

    fun targetSelected(){
        if (pathStack.isEmpty()){
            // THIS IS AN ERROR. SHOULD NOT HAPPEN
        } else {
            // The last element of the pathStack is the target.
            val targetDestination = pathStack.last()
            when (appState.value.selectedOption){
                Option.Copy.name -> {
                    selectedNodes.forEach {node -> copyNode(node, targetDestination)}
                }
                Option.Move.name -> {
                    selectedNodes.forEach {node -> moveNode(node, targetDestination)}
                }
                null -> {
                    // ERROR
                }
            }
            resetNodeSelection()
        }
    }

    fun reset(){
        Log.d("[INTERNAL_VIEW_MODEL: RESET]", "Reset Called")
        _appState.value = AppState()
        pathStack.clear()
        selectedNodes.clear()
        indexValue = 50
    }

    fun resetNodeSelection(){
        selectedNodes.clear()
        _appState.update{currentValue ->
            currentValue.copy(selectedOption = null, appMode = AppMode.Normal.name)
        }
    }

    fun setSelectedOption(option: String){
        _appState.update { currentValue ->
            currentValue.copy(selectedOption = option)
        }
    }

    fun resetSelectedOption(){
        _appState.update {currentValue ->
            currentValue.copy(selectedOption = null)
        }
    }

    fun exitVault() {
        reset()
    }
    init {
        initVaults()
    }
}