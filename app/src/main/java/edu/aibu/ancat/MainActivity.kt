package edu.aibu.ancat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.aibu.ancat.core.navigation.MainNavGraph
import edu.aibu.ancat.ui.navigation_bar.BottomNavigationBar
import edu.aibu.ancat.ui.theme.AncatTheme
import dagger.hilt.android.AndroidEntryPoint

import org.opencv.android.OpenCVLoader


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AncatTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        MainNavGraph(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController
                        )
                    }
                }
            }
        }
        if (OpenCVLoader.initLocal()) {
            Log.d("MainActivity", "OpenCV initialized")
        } else {
            Log.d("MainActivity", "OpenCV not initialized")
        }

    }
}

