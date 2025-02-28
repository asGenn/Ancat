package edu.aibu.ancat.ui.component.navigation_bar


import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.aibu.ancat.R
import edu.aibu.ancat.core.navigation.Create
import edu.aibu.ancat.core.navigation.Home
import edu.aibu.ancat.core.navigation.Survey


data class TopLevelRoute<T : Any>(val route: T, val icon: ImageVector, val label: String)

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val bottomRoutes = listOf(
        TopLevelRoute(
            route = Home,
            icon = ImageVector.vectorResource(R.drawable.ic_analyse),
            label = "Analyze"
        ),
        TopLevelRoute(
            route = Create,
            icon = ImageVector.vectorResource(R.drawable.ic_add),
            label = "Create"
        ),
        TopLevelRoute(
            route = Survey,
            icon = ImageVector.vectorResource(R.drawable.ic_questionnaire_24_24),
            label = "Survey"
        ),
    )

    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomRoutes.forEach { bottomRoute ->
            NavigationBarItem(
                icon = { Icon(imageVector = bottomRoute.icon, contentDescription = null) },
                label = { Text(bottomRoute.label) },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(route = bottomRoute.route::class) } == true,
                onClick = {
                    navController.navigate(route = bottomRoute.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }

    }
}