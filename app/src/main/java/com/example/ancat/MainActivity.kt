package com.example.ancat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ancat.core.navigation.Home
import com.example.ancat.core.navigation.MainNavGraph
import com.example.ancat.core.navigation.Profile
import com.example.ancat.ui.navigation_bar.BottomNavigationBar
import com.example.ancat.ui.theme.AncatTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AncatTheme {
                val navController = rememberNavController()
                val bottomNav = listOf(Home,Profile)
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                      BottomNavigationBar(navController)


                    }) { innerPadding ->
                   MainNavGraph(modifier = Modifier.padding(innerPadding),navController)
                }
            }
        }
    }
}

