package com.example.ancat.core.navigation

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Create

@Serializable
object Survey

@Composable
fun MainNavGraph(modifier: Modifier = Modifier,navController: NavHostController) {

    NavHost(
    navController = navController,
    startDestination = Home,
    ){
        composable<Home> {
            Box(
                modifier = modifier.fillMaxSize()
            ){
                Text("deneme")
            }
        }
        composable<Create> {
            Box(
                modifier = modifier.fillMaxSize()
            ){
                Text("2222")
            }
        }
        composable<Survey> {
            Box(
                modifier = modifier.fillMaxSize()
            ){
                Text("3333")
            }
        }

    }

}