package com.fyp.vault.ui

import FileSystem.FileSystem
import FileSystem.INode
import FileSystem.InputFile
import FileSystem.Node
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.fyp.vault.Route
import com.fyp.vault.data.ThumbnailProvider
import com.fyp.vault.utilities.MIMEFromExtension
import com.fyp.vault.utilities.createThumbnailAsStream
import com.fyp.vault.utilities.getBitmapFromStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.LinkedList

class AppViewModel(private val app: Application) : AndroidViewModel(app) {
    val vaults: MutableList<String> = mutableListOf()

    // Android FILES
    private var selectedUris: List<Uri> = emptyList()
        private set

    fun setSelectedUris(uris: List<Uri>){
        selectedUris = uris
        /*TODO Delete the code that follows*/
    }

    private var fileSystem: FileSystem? = null

    // App State
    private val _appState = MutableStateFlow(AppState())
    var appState: StateFlow<AppState> = _appState.asStateFlow()

    // File System Data
    val pathStack: MutableList<Node> = mutableStateListOf()
    val selectedNodes: MutableList<Node> = mutableStateListOf()
    /**
     * Returns a list of the names of all available vaults
     */

    fun initVaults(){
        vaults.clear()
        val baseDirectory = app.filesDir
        val directories = baseDirectory.listFiles { file -> file.isDirectory }
        if (directories != null){
            for (directory in directories){
                val superBlock = File(directory, "super-block")
                if (superBlock.isFile){
                    vaults.add(directory.name)
                }
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
            try {
                _createVault(name, password)
                navigateTo(Route.Vault.name)
            } catch (e: RuntimeException){
                /*TODO Handle Exception*/
            }
        }
    }

    private fun _createVault(name: String, password: String){
        /*TODO Remove the dummy code*/
        try {
            fileSystem = FileSystem.createFileSystem(app.filesDir, name, password)
        } catch (e: Exception){
            /*TODO Throw Error Here in the UI*/
            throw RuntimeException("Error Creating FileSystem")
        }
        _appState.value = AppState(vault = name)
        vaults.add(name)
        pathStack.clear()
        pathStack.add(_getRoot())
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
            try {
                _openVault(name, password)
                navigateTo(Route.Vault.name)
            } catch (e: RuntimeException){
                /*TODO Handle Exception*/
            }
        }
    }

    fun _openVault(name: String, password:String){
        try {
            fileSystem = FileSystem.mount(File(app.filesDir, name), password)
            _appState.value = AppState(vault = name)
            pathStack.clear()
            pathStack.add(_getRoot())
        } catch (e: Exception){
            /*TODO WORK ON ERROR*/
            throw RuntimeException("Error Mounting FileSystem")
        }
    }

    private fun _updateState(vault: String, path: String){
        _appState.update{ currentState ->
            currentState.copy(vault = vault)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
/////////   Vault Internal Operations
////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun _getThumbnail(uri: Uri): ThumbnailProvider?{
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
                var thumbnail: ThumbnailProvider? = null
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

    public fun getThumbnail(node: Node): Bitmap?{
        try {
            val thumbnailStream = fileSystem!!.openThumbnail(node)
            if (thumbnailStream == null){
                return null
            } else {
                return getBitmapFromStream(thumbnailStream)
            }
        } catch (e: Exception){
            // HANDLE ERROR
            return null
        }
    }


    private fun getInputFileFromUri(uri: Uri): InputFile{
        Log.d("[INTERNAL_VIEW_MODEL: GET_INPUT_FILE_FROM_URI]", "${uri.authority}")
        Log.d("[INTERNAL_VIEW_MODEL: GET_INPUT_FILE_FROM_URI]", "$vaults")

        var name = ""
        var parentPath: String = pathStack.last().path // FIX: CHANGE IT TO THE ACTUAL NODE getPath Method
        var size: Long = -1
        var lastModifiedTime: Long = 0  /*TODO Fix this to get the actual value*/
        var creationTime: Long = 0  /*TODO Fix this to get the actual value*/
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
        var fileInputStream: InputStream? = app.contentResolver.openInputStream(uri)
        if (fileInputStream == null) {
            throw Exception("FileInputStream is null")
        }
        if (name == "" || size == -1L){
            throw Exception("Details were not loaded")
        }
        val thumbnail = _getThumbnail(uri)
        if (thumbnail != null){
            Log.d("[INTERNAL_VIEW_MODEL: GET_INPUT_FILE_FROM_URI]", "Name: $name | Size: $size | fileinputstream: $fileInputStream | thumbnail: ${thumbnail.stream}")
            return InputFile(name, parentPath, size, creationTime, lastModifiedTime, fileInputStream, thumbnail.stream, thumbnail.size)
        } else {
            return InputFile(name, parentPath, size, creationTime, lastModifiedTime, fileInputStream);
        }
    }

    fun addFile(uri: Uri){
        // Get a Uri. Make it in a format accessible for the FileSystem.
        // Ensure that the target exists in the file system, if not create it.
        // Add the file to the filesystem
        val file = getInputFileFromUri(uri)
        Log.d("[INTERNAL_VIEW_MODEL: ADD_FILE]", "Name: ${file.name} | ParentPath: ${file.parentPath} | Size: ${file.size} | fileInputStream: ${file.fileInputStream} | thumbnailInputStream: ${file.thumbnailInputStream} | thumbnailSize: ${file.thumbnailSize}")
        /*TODO DO STUFF
        *  Call the filesystem method to add the file to the filesystem.
        * */
        try {
            fileSystem!!.addFile(pathStack.last(), file);
            _refreshPathStack()
            file.close()
        } catch (e: Exception){
            e.message?.let { Log.e("[INTERNAL_VIEW_MODEL: ADD_FILE]", it) }
            e.printStackTrace()
            // HANDLE ERROR
        }
    }

    private fun _refreshPathStack(){
        pathStack.add(pathStack.removeLast())

    }

    fun addDirectory(uri: Uri){
        // Get a Uri.
        // Create a directory for it in the filesystem if it doesn't exist.
        // Recursively add all the folders and files within the Uri to the filesystem.
    }

    fun createDirectory(name: String){
        if (fileSystem == null){
            // ERROR
        } else {
            try {
                fileSystem!!.createDirectory(pathStack.last(), name) // FIX
                _refreshPathStack()
            } catch (e: Exception){
                e.message?.let { Log.e("[INTERNAL_VIEW_MODEL: CREATE_DIRECTORY]", it) }
                e.printStackTrace()
                throw RuntimeException("Unable to Create Directory in FileSystem")
            }
        }
    }

    fun openDirectory(node: Node){
        try {
            fileSystem!!.getNode(node)
            pathStack.add(node)
        } catch (e: Exception){
            // HANDLE ERROR
        }
    }

    fun getChildNodes(node: Node): LinkedList<Node> {
        if (node.childrenNotRead()){
            try {
                return fileSystem!!.openDirectory(node)
            } catch (e: Exception){
                // HANDLE ERROR
                return LinkedList<Node>()   // REMOVE
            }
        } else {
            return node.childNodes
        }
    }

    fun copyNode(node: Node, targetDestination: Node){
        Log.d("[INTERNAL_VIEW_MODEL: COPY_NODE]", "Node: ${node.name} | TargetDestination: ${targetDestination.name}")
    }

    fun moveNode(node: Node, targetDestination: Node){
        Log.d("[INTERNAL_VIEW_MODEL: MOVE_NODE]", "Node: ${node.name} | TargetDestination: ${targetDestination.name}")
    }

    fun openFile(node: Node){

        /*TODO Future Implementations will have openers for images and videos.
        *  For now, just create temporary files and open them that way.*/
        if (node.isDirectory){
            return
        }
        val name = node.name.split(".").getOrNull(0)
        var extension = node.name.split(".").getOrNull(1) ?: return
        extension = ".$extension"
        Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "Name: $name, Extension: $extension")
        try {
            val tempFile = File.createTempFile("temp-$name", extension, app.cacheDir)
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "tempFile: $tempFile")
            FileOutputStream(tempFile).use {outputStream ->
                fileSystem!!.openFile(node).copyTo(outputStream)
                Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "File Written")
            }
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "File exists: ${tempFile.exists()}")
            val uri: Uri = FileProvider.getUriForFile(app.applicationContext, "${app.packageName}.fileprovider", tempFile)
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "URI: $uri")
            var mime = MIMEFromExtension(extension) ?: return
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "Mime: $mime")
            mime = mime.split("/")[0]
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "Mime start: $mime")
            val intent = Intent(Intent.ACTION_VIEW).apply{
                setDataAndType(uri, "$mime/*")
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_GRANT_READ_URI_PERMISSION))
            }
            Log.d("[INTERNAL_VIEW_MODEL: OPEN_FILE]", "Intent: $intent")
            if (intent.resolveActivity(app.baseContext.packageManager) != null){
                app.baseContext.startActivity(intent)
            }
            tempFile.deleteOnExit()
        } catch (e: Exception){
            e.message?.let { Log.e("[INTERNAL_VIEW_MODEL: OPEN_FILE]", it) }
        }
    }

    fun getSize(node: Node): Long{
        try {
            val iNode: INode? = fileSystem!!.getINode(node)
            if (iNode == null){
                // Node is a directory. Return -1
                return -1
            } else {
                return iNode.getiNodeSize()
            }
        } catch (e: Exception){
            // HANDLE ERROR
            e.message?.let { Log.e("[INTERNAL_VIEW_MODEL: GET_SIZE]", it) }
            return -1;
        }
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
            /*TODO REPLACE THIS WITH THE ACTUAL FILE SYSTEM CALL*/
            createDirectory(value);
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

    fun _getRoot(): Node{
        /*TODO Replace with logic to get real root from the actual file system*/
        return fileSystem!!.dir.root
    }

    fun toggleIsListView(){
        _appState.update{ currentValue ->
            currentValue.copy(isListView = !_appState.value.isListView)
        }
    }

    private fun renameNode(node: Node, name: String){
        Log.d("[INTERNAL_VIEW_MODEL: RENAME_NODE]", name)
        // IT DOES REACH THIS POINT
        // RENAME DOESN'T HAPPEN FOR SOME REASON
//        node.name = name
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
                    createDirectory(value)
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
        val directory = pathStack.lastOrNull()
        if (directory != null){
            for (node in directory.childNodes){
                addNodeToSelection(node)
            }
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
        fileSystem = null
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