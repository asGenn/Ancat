package edu.aibu.ancat.ui.views.analysis_result_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyAnalysisResult
import edu.aibu.ancat.data.model.SurveyItem
import java.util.Locale

@Composable
fun AnalysisResultScreen(path: String, title: String) {
    val viewModel: AnalysisResultScreenViewModel = hiltViewModel()
    val jsonResultData = remember { mutableStateOf<List<SurveyAnalysisResult>>(emptyList()) }
    val jsonQuestionData = remember { mutableStateOf<List<SurveyItem>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val questionData = viewModel.getJsonQuestionData(path, context)
        val resulData = viewModel.getJsonResultData(path, context)
        jsonResultData.value = resulData
        jsonQuestionData.value = questionData
    }

    LazyColumn {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        jsonResultData.value.forEach { result ->
            val totalVotes = result.analysis.sum().takeIf { it > 0 } ?: 1

            item {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        when (result.type) {
                            "2" -> {
                                val quest =
                                    jsonQuestionData.value[result.sectionIdx!!].questions[result.questionIdx!!] as Question.MultipleChoiceQuestion
                                Text(
                                    text = quest.question,
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                                if (result.analysis.isEmpty()) {
                                    Text(
                                        text = "Bu soru henüz analiz edilmedi.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                } else {
                                    result.analysis.forEachIndexed { idx, count ->
                                        val percent = (count * 100f / totalVotes)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = quest.options[idx],
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "$count oy",
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = String.format(Locale.US, "%.1f%%", percent),
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                            )
                                        }
                                    }
                                }
                            }

                            "1" -> {
                                val quest =
                                    jsonQuestionData.value[result.sectionIdx!!].questions[result.questionIdx!!] as Question.RatingQuestion
                                Text(
                                    text = quest.question,
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (result.analysis.isEmpty()) {
                                    Text(
                                        text = "Bu soru henüz analiz edilmedi.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                } else {
                                    result.analysis.forEachIndexed { idx, count ->
                                        val percent = (count * 100f / totalVotes)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Puan ${5 - idx}",
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "$count oy",
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = String.format(Locale.US, "%.1f%%", percent),
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                            )
                                        }
                                    }
                                }
                            }

                            else -> {
                                Text(text = "Bilinmeyen tür: ${result.type}")
                            }
                        }
                    }
                }
            }
        }
    }
}
