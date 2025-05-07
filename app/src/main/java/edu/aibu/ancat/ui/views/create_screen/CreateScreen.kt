package edu.aibu.ancat.ui.views.create_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.components.CustomFloatingActionButton
import edu.aibu.ancat.ui.views.create_screen.components.EmptyScreen
import edu.aibu.ancat.ui.views.create_screen.components.JsonFileListScreen
import edu.aibu.ancat.ui.views.create_screen.components.SurveyTitleDialog

/**
 * Ana anket oluşturma ekranı
 *
 * @param navController Navigasyon kontrolcüsü
 */
@Composable
fun CreateScreen(navController: NavController) {
    val viewModel: CreateScreenViewModel = hiltViewModel()
    val jsonFilesList = remember { mutableStateListOf<JsonFilesInfoEntity>() }
    val openDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val files = viewModel.getJsonFiles()
        jsonFilesList.addAll(files)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (jsonFilesList.isEmpty()) {
            EmptyScreen(
                openDialog = openDialog
            )
        } else {
            JsonFileListScreen(
                modifier = Modifier,
                navController = navController,
                jsonFilesList = jsonFilesList,
                context = context,
                viewModel = viewModel
            )
        }

        if (jsonFilesList.isNotEmpty()) {
            CustomFloatingActionButton(
                onClick = { openDialog.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = Icons.Rounded.Add,
                contentDescription = "Anket Oluştur"
            )
        }
    }


    SurveyTitleDialog(
        openDialog = openDialog,
        navController = navController,
        context = context,
        viewModel = viewModel
    )
}

