package com.fyp.vault.ui.components

import FileSystem.Node
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.fyp.vault.R
import com.fyp.vault.ui.theme.VaultTheme
import java.util.LinkedList

@Composable
fun NavigationBar(
    pathStack: MutableList<Node>,
    onNavigationButtonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()
    // Scroll to the very end of the list once it is rendered
    LaunchedEffect(Unit) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .horizontalScroll(scrollState)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        if (pathStack.size > 1){
            for (index in pathStack.dropLast(1).indices){
                NavigationButton(directory = pathStack[index], onClick = {onNavigationButtonClick(index)})
                NavigationSeparator()
            }
        }
        if (pathStack.isNotEmpty()){
            NavigationButton(
                directory = pathStack.last(),
                onClick = {onNavigationButtonClick(pathStack.size - 1)},
                currentDirectory = true
            )
        }
    }
}

@Composable
fun NavigationButton(
    directory: Node,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentDirectory: Boolean = false
){
    TextButton(onClick = onClick) {
        if (currentDirectory){
            Text(
                text = if (directory.name == "root") stringResource(id = R.string.root_directory_name) else directory.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier,
                fontWeight = FontWeight.ExtraBold
            )
        } else {
            Text(
                text = if (directory.name == "root") stringResource(id = R.string.root_directory_name) else directory.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary.copy(.8f),
                modifier = modifier
            )
        }
    }
}

@Composable
fun NavigationSeparator(
    modifier: Modifier = Modifier,
    separator: String = ">"
){
    Text(
        text = separator,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary.copy(0.8f),
        modifier = modifier
    )
}
//
//
//@Preview(
//    showBackground = true,
//    name = "NavigationBarPreview LightMode",
//    uiMode = Configuration.UI_MODE_NIGHT_NO
//)
//@Preview(
//    showBackground = true,
//    name = "NavigationBarPreview DarkMode",
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun NavigationBarPreview(){
//    val pathStack = LinkedList<Directory>()
//    pathStack.add(root)
//    pathStack.add(root.childrenDirectories.first())
//    VaultTheme {
//        NavigationBar(
//            pathStack = pathStack,
//            onNavigationButtonClick = {},
//            modifier = Modifier
//                .padding(dimensionResource(id = R.dimen.padding_medium))
//                .fillMaxWidth()
//        )
//    }
//}