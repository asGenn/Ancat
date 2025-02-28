package edu.aibu.ancat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.aibu.ancat.core.navigation.MainNavGraph
import edu.aibu.ancat.ui.component.navigation_bar.BottomNavigationBar
import edu.aibu.ancat.ui.theme.AncatTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AncatTheme {
                val navController = rememberNavController()
                Box(modifier = Modifier.fillMaxSize()) {
                    MainNavGraph(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                    BottomNavigationBar(
                        navController = navController,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

