package edu.aibu.ancat.ui.navigation_bar


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomRoutes.forEach { bottomRoute ->
                val isSelected = currentDestination?.hierarchy?.any { 
                    it.hasRoute(route = bottomRoute.route::class) 
                } == true
                
                val iconSize by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 24.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconSize"
                )

                val interactionSource = remember { MutableInteractionSource() }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null // Ripple efektini kaldırmak için null
                        ) {
                            navController.navigate(route = bottomRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) { 
                                    saveState = true 
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = bottomRoute.icon, 
                            contentDescription = null,
                            modifier = Modifier.size(iconSize),
                            tint = if (isSelected) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            Text(
                                text = bottomRoute.label,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}