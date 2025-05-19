package edu.aibu.ancat.ui.views.analysis_result_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.data.model.SurveyAnalysisResult


@Composable
fun AnalysisResultScreen(path: String, title: String) {
    val viewModel: AnalysisResultScreenViewModel = hiltViewModel()
    val jsonResultData = remember { mutableStateOf<List<SurveyAnalysisResult>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val data = viewModel.getJsonResultData(path, context)
        jsonResultData.value = data
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        jsonResultData.value.forEach { result ->
            val totalVotes = result.analysis.sum().takeIf { it > 0 } ?: 1
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Soru ${(result.questionIdx ?: 0) + 1}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    result.analysis.forEachIndexed { idx, count ->
                        val percent = (count * 100f / totalVotes)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Seçenek ${idx + 1}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$count oy",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = String.format("%.1f%%", percent),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
