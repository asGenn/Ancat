package com.example.ancat.core.navigation

import AnalyzeScreen
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.ancat.ui.views.create_screen.CreateScreen
import com.example.ancat.ui.views.create_survey_screen.CreateSurveyScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

// nested graph for create screen
@Serializable
object CreateNested

// nested graph start
@Serializable
object Create

@Serializable
data class CreateSurvey(val title: String, val id: Int? = null)

@Serializable
object Survey


@Composable
fun MainNavGraph(modifier: Modifier = Modifier, navController: NavHostController) {

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = modifier.padding(innerPadding)
        ) {

            composable<Home> {
                AnalyzeScreen()
            }

            navigation<CreateNested>(startDestination = Create) {
                composable<Create> {
                    CreateScreen(navController)
                }
                composable<CreateSurvey> { backStackEntry ->
                    val createSurvey: CreateSurvey = backStackEntry.toRoute()
                    CreateSurveyScreen(id = createSurvey.id)
                }
            }

            composable<Survey> {
                Box(
                    modifier = modifier.fillMaxSize()
                ) {
                    Text("Surveys Screen")
                }
            }

        }
    }

}