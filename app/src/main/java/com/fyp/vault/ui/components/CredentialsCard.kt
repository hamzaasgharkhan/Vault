package com.fyp.vault.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.fyp.vault.R
import com.fyp.vault.ui.Error

@Composable
fun CredentialsCard(
    /*TODO Add UI State*/
    name: String,
    password: String,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    error: Error?,
    clearError: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isNameEditable: Boolean = true
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ){
        VaultTextField(
            value = name,
            placeholder = stringResource(R.string.vault_name_placeholder),
            label = stringResource(R.string.vault_name_placeholder),
            errorLabel = stringResource(R.string.vault_name_placeholder_error),
            onValueChange = {
                onNameChange(it)
                if (error != null){
                    clearError()
                }
            },
            onSubmit = onSubmit,
            isError = error != null,
            isEnabled = isNameEditable
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        VaultTextField(
            value = password,
            placeholder = stringResource(R.string.vault_password_placeholder),
            label =  stringResource(R.string.vault_password_placeholder),
            errorLabel = stringResource(R.string.vault_password_placeholder_error),
            onValueChange = {
                onPasswordChange(it)
                if (error != null){
                    clearError()
                }
            },
            onSubmit = onSubmit,
            isError = error != null,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        if (error != null){
            Text(
                text = "* " + error.description,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth(.75f),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
