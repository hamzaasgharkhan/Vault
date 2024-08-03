package com.fyp.vault.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.fyp.vault.R
import com.fyp.vault.data.Node
import com.fyp.vault.ui.Option
import com.fyp.vault.ui.theme.VaultTheme
import java.util.LinkedList

@Composable
fun Dialog(
    selectedOption: String,
    onSubmit: (Node?, String, String) -> Unit,
    closeDialog: () -> Unit,
    modifier: Modifier = Modifier,
    selectedNodes: MutableList<Node>
){
    when (selectedOption){
        Option.CreateDirectory.name -> {
            SingleInputDialog(
                title = R.string.create_directory_title,
                placeholder = R.string.create_directory_placeholder,
                label = R.string.create_directory_label,
                selectedOption = selectedOption,
                onSubmit = onSubmit,
                closeDialog = closeDialog,
                modifier = modifier
            )
        }
        Option.Rename.name -> {
            SingleInputDialog(
                title = R.string.rename_title,
                placeholder = R.string.rename_placeholder,
                label = R.string.rename_label,
                selectedOption = selectedOption,
                onSubmit = onSubmit,
                selectedNode = selectedNodes.first(),
                closeDialog = closeDialog
            )
        }
        Option.Delete.name -> {
            ConfirmDialog(
                title = R.string.delete_title,
                text = R.string.delete_text,
                selectedOption = selectedOption,
                onSubmit = onSubmit,
                closeDialog = closeDialog,
                selectedNodes = selectedNodes
            )
        }
        Option.ExitVault.name -> {
            ConfirmDialog(
                title = R.string.confirm_exit_vault_title,
                text = R.string.confirm_exit_vault_text,
                selectedOption = selectedOption,
                onSubmit = onSubmit,
                closeDialog = closeDialog,
                selectedNodes = selectedNodes
            )
        }
    }
}

@Composable
fun SingleInputDialog(
    @StringRes title: Int,
    @StringRes placeholder: Int,
    @StringRes label: Int,
    selectedOption: String,
    onSubmit: (Node?, String, String) -> Unit,
    closeDialog: () -> Unit,
    modifier: Modifier = Modifier,
    selectedNode: Node? = null
) {
    var name by rememberSaveable { mutableStateOf(selectedNode?.name ?: "") }
    AlertDialog(
        title = {
            Text(text = stringResource(title))
        },
        text = {
            OutlinedTextField(
                value = name,
                placeholder = {
                    Text(text = stringResource(placeholder))
                },
                label = {
                    Text(text = stringResource(label))
                },
                onValueChange = { name = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSubmit(selectedNode, selectedOption, name)
                        closeDialog()
                    },
                )
            )
        },
        onDismissRequest = closeDialog,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(selectedNode, selectedOption, name)
                closeDialog()
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = closeDialog ) {
                Text(text = "Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
fun ConfirmDialog(
    @StringRes title: Int,
    @StringRes text: Int,
    selectedOption: String,
    onSubmit: (Node?, String, String) -> Unit,
    closeDialog: () -> Unit,
    modifier: Modifier = Modifier,
    selectedNodes: MutableList<Node>
) {
    AlertDialog(
        title = {
            Text(text = stringResource(title))
        },
        text = {
            Column(){
                Text(text = stringResource(text))
                when (selectedOption){
                    Option.Delete.name -> {
                        Text(text = "(${selectedNodes.size} Item${if (selectedNodes.size == 1) "" else "s"})", modifier = Modifier.align(Alignment.End))
                        if (selectedNodes.size > 5){
                            for (i in 0..4){
                                Text(
                                    text = selectedNodes[i].name,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(text = "...")
                        } else {
                            selectedNodes.forEach{ node ->
                                Text(
                                    text = node.name,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = closeDialog,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(null, selectedOption, "")
                closeDialog()
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = closeDialog ) {
                Text(text = "Cancel")
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun SingleInputDialogPreview(){
    VaultTheme {
        SingleInputDialog(
            title = R.string.create_directory_placeholder,
            placeholder = R.string.create_directory_placeholder,
            label = R.string.create_directory_label,
            selectedOption = Option.CreateDirectory.name,
            onSubmit = { _ ,  _ , _ -> },
            closeDialog = { /*TODO*/ })
    }
}

@Preview
@Composable
fun ConfirmDialogPreview(){
    val nodes = LinkedList<Node>()
    nodes.add(Node("Test Node", 1))
    nodes.add(Node("Test Node", 1))
    nodes.add(Node("Test Node", 1))
    nodes.add(Node("Test Node", 1))
    nodes.add(Node("Test Node", 1))
    nodes.add(Node("Test Node", 1))

    VaultTheme {
        ConfirmDialog(
            title = R.string.delete_title,
            text = R.string.delete_text,
            selectedOption = Option.CreateDirectory.name,
            onSubmit = { _, _, _ -> },
            closeDialog = { /*TODO*/ },
            selectedNodes = nodes
        )
    }
}