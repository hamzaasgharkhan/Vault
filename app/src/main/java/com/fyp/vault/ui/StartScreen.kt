package com.fyp.vault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.fyp.vault.R
import com.fyp.vault.ui.components.TopBar
import com.fyp.vault.ui.components.VaultClickableCard
import com.fyp.vault.ui.theme.VaultTheme

@Composable
fun StartScreen(
    vaults: List<String>,
    onCreateVault: () -> Unit,
    onVaultClick: (String) -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.start_title),
                canNavigateBack = false
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateVault
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.create_vault_button)
                )
            }
        },
        modifier = modifier
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (vaults.isEmpty()){
                Text(
                    text = stringResource(id = R.string.no_vaults),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                vaults.forEach{ title ->
                    VaultClickableCard(
                        title = title,
                        onCardClick = {onVaultClick(title)},
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StartScreenPreview(){
    VaultTheme{
        StartScreen(vaults = listOf(), onCreateVault = {}, onVaultClick = {})
    }
}

