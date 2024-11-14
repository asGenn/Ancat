package com.example.ancat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ancat.core.navigation.MainNavGraph
import com.example.ancat.ui.component.navigation_bar.BottomNavigationBar
import com.example.ancat.ui.theme.AncatTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AncatTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier,
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    MainNavGraph(modifier = Modifier.padding(innerPadding), navController)
                }
            }
        }
    }
}

