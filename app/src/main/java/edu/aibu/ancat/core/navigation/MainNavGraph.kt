package edu.aibu.ancat.core.navigation

import edu.aibu.ancat.ui.views.home_screen.AnalyzeScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import edu.aibu.ancat.ui.views.create_screen.CreateScreen
import edu.aibu.ancat.ui.views.create_survey_screen.CreateSurveyScreen
import kotlinx.serialization.Serializable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import edu.aibu.ancat.ui.views.test_screen.TestView

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

    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {

        composable<Home>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            AnalyzeScreen()
        }

        navigation<CreateNested>(startDestination = Create) {
            composable<Create>(
                enterTransition = {
                    val initialRoute = initialState.destination.route?.substringBefore("/") ?: ""
                    val direction = if (initialRoute.contains("Survey")) {
                        AnimatedContentTransitionScope.SlideDirection.Right
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    }
                    
                    slideIntoContainer(
                        towards = direction,
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    val targetRoute = targetState.destination.route?.substringBefore("/") ?: ""
                    val direction = if (targetRoute.contains("Survey")) {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    } else {
                        AnimatedContentTransitionScope.SlideDirection.Left
                    }
                    
                    slideOutOfContainer(
                        towards = direction,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                }
            ) {
                CreateScreen(navController)
            }
            composable<CreateSurvey> { backStackEntry ->
                val createSurvey: CreateSurvey = backStackEntry.toRoute()
                CreateSurveyScreen(id = createSurvey.id)
            }
        }

        composable<Survey>(
            enterTransition = {
                val initialRoute = initialState.destination.route?.substringBefore("/") ?: ""
                val direction = if (initialRoute.contains("Create")) {
                    AnimatedContentTransitionScope.SlideDirection.Left
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Left
                }
                
                slideIntoContainer(
                    towards = direction,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                val targetRoute = targetState.destination.route?.substringBefore("/") ?: ""
                val direction = if (targetRoute.contains("Create")) {
                    AnimatedContentTransitionScope.SlideDirection.Right
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Left
                }
                
                slideOutOfContainer(
                    towards = direction,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) {
            TestView()
        }

    }
}