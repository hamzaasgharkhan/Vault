package com.fyp.vault.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun VaultTextField(
    value: String,
    placeholder: String,
    label: String,
    errorLabel: String = "",
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isError: Boolean = false,
    isPassword: Boolean = false,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
){
    var showPassword by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        /*TODO Add UI State instead of Fixed values*/
        /*TODO Fix the event handlers*/
        value = value,
        placeholder = {
            Text(text = placeholder)
        },
        label = {
            if (isError){
                Text(text = errorLabel)
            } else {
                Text(text = label)
            }
        },
        onValueChange = {onValueChange(it)},
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            if (isPassword){
                IconButton(onClick = { showPassword = !showPassword}) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null)
                }
            }
        },
        visualTransformation =
            if (isPassword && !showPassword)
                {PasswordVisualTransformation()}
            else
                {VisualTransformation.None},
        keyboardActions = KeyboardActions(
            onDone = {onSubmit()},
        ),
        isError = isError,
        enabled = isEnabled,
        modifier = modifier
    )
}