package com.fyp.vault.ui

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
import com.fyp.vault.ui.components.CredentialsCard
import com.fyp.vault.ui.components.TopBar

@Composable
fun CreateVaultScreen(
    navigateUp: () -> Unit,
    onVaultCreate: (String, String) -> Unit,
    error: Error?,
    modifier: Modifier = Modifier,
    backHandler: () -> Unit
){
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    /*TODO Fix Event Handlers*/
    /*TODO Fix states*/
    /*TODO Pass error to others*/
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.create_vault_title),
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
                    name = name,
                    password = password,
                    onNameChange = {name = it},
                    onPasswordChange = {password = it},
                    onSubmit = {onVaultCreate(name, password)},
                    error = error,
                    modifier = Modifier.align(Alignment.Center)
                )
                Button(
                    onClick = { onVaultCreate(name, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = stringResource(R.string.create_vault_button),
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }
    BackHandler {
        backHandler()
    }
}