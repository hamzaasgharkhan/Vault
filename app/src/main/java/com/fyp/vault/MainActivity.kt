package com.fyp.vault

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fyp.vault.ui.AppViewModel
import com.fyp.vault.ui.theme.VaultTheme

class MainActivity : ComponentActivity() {
    private lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this, InternalViewModelFactory(application)).get(AppViewModel::class.java)
        enableEdgeToEdge()
        setContent {
            VaultTheme {
                    VaultApp(
                        appViewModel = appViewModel
                    )
            }
        }
    }
}

//class ViewModelFactory(private val externalViewModel: ExternalViewModel) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(InternalViewModel::class.java)){
//            @Suppress("UNCHECKED_CAST")
//            return InternalViewModel(externalViewModel) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel Class")
//    }
//}

class InternalViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}