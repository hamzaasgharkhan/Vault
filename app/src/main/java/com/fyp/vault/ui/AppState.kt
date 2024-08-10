package com.fyp.vault.ui

import com.fyp.vault.Route

data class AppState(
    val vault: String = "",
    val route: String = Route.Start.name,
    val isListView: Boolean = true,
    val showDialog: Boolean = false,
    val showDialogOption: String? = null,
    val appMode: String = AppMode.Normal.name,
    val selectedOption: String? = null,
    val error: Error? = null
)

data class VaultAccessState(
    val name: String = "",
    val password: String = "",
)

enum class AppMode(){
    Normal,
    Selection,
    TargetPicker
}

enum class Option(val label: String){
    CreateDirectory("Create New Folder"),
    SelectAll("Select All"),
    Select("Select"),
    Rename("Rename"),
    Move("Move to"),
    Copy("Copy to"),
    Delete("Delete"),
    Export("Export"),
    DeSelectAll("Deselect All"),
    SelectTarget("Select Target Destination"),
    Cancel("Cancel"),
    ExitVault("Exit Vault"),
    ExportVault("Export Vault"),
    DeleteVault("Delete Vault")
}

enum class AddNodeType{
    Media,
    File,
//    Directory
}

enum class Error(val description: String){
    CreateVault_InvalidName_AlreadyExists("Vault Already Exists"),
    CreateVault_InvalidName_Blank("Name Cannot Be Blank"),
    CreateVault_InvalidPassword_Blank("Password Cannot Be Blank"),
    OpenVault_InvalidName_DoesNotExist("Vault Does Not Exist"),
    OpenVault_InvalidName_Blank("Name Cannot Be Blank"),
    OpenVault_InvalidPassword_Blank("Password Cannot Be Blank"),
    OpenVault_InvalidPassword("Invalid Password"),
    Node_Rename_NameAlreadyExists("The Following Name Already Exists"),
    Node_Rename_Blank("Name Cannot Be Blank"),
    Node_Copy_Circular("Cannot Be Copied Within Itself"),
    Node_Move_Circular("Cannot Be Moved Within Itself")
}


enum class OptionCategory(val options: List<Option>){
    SingleNodeOptions(listOf(
        Option.Select,
    )),
    NodeOptions(listOf(
        Option.Rename,
        Option.Move,
        Option.Copy,
        Option.Delete,
        Option.Export
    )),
    TopBarOptions(listOf(
        Option.SelectAll,
        Option.CreateDirectory
    )),
    SelectionModeOptions(listOf(
        Option.SelectAll,
        Option.Move,
        Option.Copy,
        Option.Delete,
        Option.Export
    )),
    SelectionModeOptionsOnAllSelected(listOf(
        Option.DeSelectAll
    )),
    TargetPickerModeOptions(listOf(
        Option.CreateDirectory,
        Option.Cancel
    )),
    VaultOptions(listOf(
        Option.ExportVault,
        Option.DeleteVault
    )),
    ToastOptions(listOf(
        Option.Rename,
        Option.Copy,
        Option.Move,
        Option.Delete,
        Option.Export,
        Option.ExportVault,
        Option.CreateDirectory
    ))
}