package edu.aibu.ancat.ui.views.analysis_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import edu.aibu.ancat.ui.views.analysis_screen.components.AnalyzedFileListScreen
import edu.aibu.ancat.ui.views.analysis_screen.components.UnanalyzedScreen

@Composable
fun AnalysisScreen(navController: NavController) {
    val viewModel: AnalysisScreenViewModel = hiltViewModel()
    val jsonAnalysisList = remember { mutableStateListOf<Pair<String, String>>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val files = viewModel.getAnalysisFiles(context = context)
        jsonAnalysisList.addAll(files)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (jsonAnalysisList.isEmpty()) {
            UnanalyzedScreen()
        } else {
            AnalyzedFileListScreen(navController, jsonAnalysisList)
        }
    }

}