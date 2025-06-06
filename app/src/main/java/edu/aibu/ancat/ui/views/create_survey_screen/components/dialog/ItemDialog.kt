package edu.aibu.ancat.ui.views.create_survey_screen.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.ui.views.create_survey_screen.CreateSurveyViewModel

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
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {

            LazyColumn(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                itemsIndexed(text) { index, optionText ->
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Hafif belirgin bir renk önerisi
                            focusedIndicatorColor = Color.Transparent, // Alt çizgiyi gizler
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary, // Gözle görülür bir imleç
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f) // Devre dışı durum
                        ),
                        value = optionText,
                        onValueChange = {
                            text[index] = it

                            if (it.isEmpty() && it.isBlank())
                                text.removeAt(text.lastIndex)

                            if (index == text.lastIndex && it.isNotBlank())
                                text.add("")

                        },
                        label = { Text("Açıklama ${index + 1}") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .height(48.dp)
                            .width(90.dp),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        ),
                        onClick = {
                            text.removeAt(text.lastIndex)
                            val descList = mutableListOf<Question.SurveyDescription>()
                            text.forEach {
                                descList.add(Question.SurveyDescription(description = it))
                            }
                            if (text.isNotEmpty()) {
                                val survey = SurveyItem(
                                    type = "0",
                                    questions = descList.toList(),
                                )
                                viewModel.addSurveyItem(survey)
                            }
                            onDismissRequest()
                        }) {
                        Text("Ekle")
                    }
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
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {

            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Hafif belirgin bir renk önerisi
                        focusedIndicatorColor = Color.Transparent, // Alt çizgiyi gizler
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary, // Gözle görülür bir imleç
                        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f) // Devre dışı durum
                    ),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    label = { Text("Soru") },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .height(48.dp)
                        .width(90.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    ),
                    onClick = {
                        if (text.isNotEmpty() && text.isNotBlank()) {
                            val survey = SurveyItem(
                                type = "1",
                                questions = listOf(
                                    Question.RatingQuestion(
                                        question = text,
                                        mark = 0f,
                                    ),
                                )
                            )
                            viewModel.addSurveyItem(survey)
                        }

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
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {

            LazyColumn(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
            ) {
                item {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Hafif belirgin bir renk önerisi
                            focusedIndicatorColor = Color.Transparent, // Alt çizgiyi gizler
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary, // Gözle görülür bir imleç
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f) // Devre dışı durum
                        ),
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Soru") },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(options) { index, optionText ->
                    TextField(
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Hafif belirgin bir renk önerisi
                            focusedIndicatorColor = Color.Transparent, // Alt çizgiyi gizler
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary, // Gözle görülür bir imleç
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f) // Devre dışı durum
                        ),
                        value = optionText,
                        onValueChange = {
                            options[index] = it

                            if (it.isEmpty() && it.isBlank())
                                options.removeAt(options.lastIndex)

                            if (index == options.lastIndex && it.isNotBlank())
                                options.add("")
                        },
                        label = { Text("Seçenek ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .height(48.dp)
                            .width(90.dp),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        ),
                        onClick = {
                            options.removeAt(options.lastIndex)
                            if (options.isNotEmpty() && questionText.isNotEmpty() && questionText.isNotBlank()) {
                                val survey = SurveyItem(
                                    type = "2",
                                    questions = listOf(
                                        Question.MultipleChoiceQuestion(
                                            question = questionText,
                                            options = options,
                                            marks = emptyList(),
                                        )
                                    )
                                )
                                viewModel.addSurveyItem(survey)
                            }
                            onDismissRequest()
                        }) {
                        Text("Ekle")
                    }
                }
            }
        }
    }
}
