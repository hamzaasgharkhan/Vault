package com.fyp.vault.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.fyp.vault.R
import com.fyp.vault.ui.components.CircularProgressOverlay
import com.fyp.vault.ui.components.CredentialsCard
import com.fyp.vault.ui.components.TopBar

@Composable
fun OpenVaultScreen(
    navigateUp: () -> Unit,
    onVaultOpen: (String, String) -> Unit,
    inputName: String,
    error: Error?,
    modifier: Modifier = Modifier,
    backHandler: () -> Unit
){
    var loading by rememberSaveable { mutableStateOf(false) }
    Log.d("[OPEN_VAULT_SCREEN]", "Name: $inputName")
    var password by rememberSaveable { mutableStateOf("") }
    /*TODO Fix Event Handlers*/
    /*TODO Fix states*/
    /*TODO Pass error to others*/
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.open_vault_title),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ){
                CredentialsCard(
                    modifier = Modifier.align(Alignment.Center),
                    name = inputName,
                    password = password,
                    onNameChange = {},
                    onPasswordChange = {password = it},
                    onSubmit = {
                        loading = true
                        onVaultOpen(inputName, password)
                        loading = false
                    },
                    error = error,
                    isNameEditable = false
                )
                Button(
                    onClick = {
                        loading = true
                        onVaultOpen(inputName, password)
                        loading = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = stringResource(R.string.open_vault_button),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }
    if (loading){
        CircularProgressOverlay()
    }
    BackHandler {
        if (!loading){
            backHandler()
        }
    }
}