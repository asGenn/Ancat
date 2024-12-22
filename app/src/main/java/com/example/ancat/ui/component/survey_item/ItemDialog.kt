package com.example.ancat.ui.component.survey_item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ancat.data.model.Question
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.ui.views.create_survey_screen.CreateSurveyViewModel

@Composable
fun SimpleQuestionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    viewModel: CreateSurveyViewModel
) {
    val text = remember { mutableStateListOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {

        Box(
            modifier = modifier
                .background(color = Color.White)
                .fillMaxSize(),
        ) {

            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                text.forEachIndexed { index, optionText ->
                    TextField(
                        value = optionText,
                        onValueChange = {
                            text[index] = it
                            if (index == text.lastIndex && it.isNotBlank()) {
                                text.add("")
                            }
                        },
                        label = { Text("Açıklama ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    text.removeAt(text.lastIndex)
                    val survey = SurveyItem(
                        type = "0",
                        title = "",
                        questions = listOf(
                            Question.SurveyDescription(
                                description = text
                            )
                        ),
                    )
                    viewModel.addSurveyItem(survey)
                    onDismissRequest()
                }) {
                    Text("Ekle")
                }
            }
        }

    }

}

@Composable
fun RatingQuestionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    viewModel: CreateSurveyViewModel
) {
    var text by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {

        Box(
            modifier = modifier
                .background(color = Color.White)
                .fillMaxSize(),
        ) {

            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it

                    },
                    label = { Text("Soru") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val survey = SurveyItem(
                        type = "1",
                        title = "",
                        questions = listOf(
                            Question.RatingQuestion(
                                question = text
                            ),
                        )
                    )
                    viewModel.addSurveyItem(survey)
                    onDismissRequest()
                }) {
                    Text("Ekle")
                }
            }
        }

    }
}

@Composable
fun MultipleChoiceQuestionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    viewModel: CreateSurveyViewModel
) {
    var questionText by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("") }  // İlk boş seçenek alanı

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .background(color = Color.White)
                .fillMaxSize(),
        ) {

            Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                TextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Soru") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                options.forEachIndexed { index, optionText ->
                    TextField(
                        value = optionText,
                        onValueChange = {
                            options[index] = it

                            if (index == options.lastIndex && it.isNotBlank()) {
                                options.add("")
                            }
                        },
                        label = { Text("Seçenek ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    options.removeAt(options.lastIndex)

                    val survey = SurveyItem(
                        type = "2",
                        title = "",
                        questions = listOf(
                            Question.MultipleChoiceQuestion(
                                question = questionText,
                                options = options
                            )
                        )
                    )
                    viewModel.addSurveyItem(survey)
                    onDismissRequest()
                }) {
                    Text("Ekle")
                }
            }
        }
    }
}
