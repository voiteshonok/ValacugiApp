package by.voiteshonok.valacugi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import by.voiteshonok.valacugi.core.di.AppContainer
import by.voiteshonok.valacugi.core.navigation.AppNavHost
import by.voiteshonok.valacugi.ui.theme.ValacugiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = AppContainer(context = applicationContext)
        setContent {
            ValacugiTheme {
                AppNavHost(
                    modifier = Modifier.fillMaxSize(),
                    appContainer = appContainer
                )
            }
        }
    }
}