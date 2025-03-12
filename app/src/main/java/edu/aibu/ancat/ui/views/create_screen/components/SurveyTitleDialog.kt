package edu.aibu.ancat.ui.views.create_screen.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.navigation.CreateSurvey
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.ui.views.create_screen.CreateScreenViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Anket başlığı oluşturma dialogu
 *
 * @param openDialog Dialog açık/kapalı durumu
 * @param navController Navigasyon kontrolcüsü
 * @param context Context
 * @param viewModel ViewModel
 */
@Composable
fun SurveyTitleDialog(
    openDialog: MutableState<Boolean>,
    navController: NavController,
    context: Context,
    viewModel: CreateScreenViewModel
) {
    var title by remember { mutableStateOf("") }
    val descriptions = remember { mutableStateListOf("") }
    val surveyItemList = remember { mutableStateListOf<SurveyItem>() }

    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 300.dp, max = 500.dp)
                .clip(RoundedCornerShape(24.dp)),
            onDismissRequest = {
                openDialog.value = false
                title = ""
                descriptions.clear()
                descriptions.add("")
            },
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Yeni Anket",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Anket Başlığı") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "Açıklamalar",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(descriptions) { index, desc ->
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = desc,
                                onValueChange = {
                                    descriptions[index] = it
                                    if (it.isEmpty() && descriptions.size > 1)
                                        descriptions.removeAt(index)
                                    if (index == descriptions.lastIndex && it.isNotBlank())
                                        descriptions.add("")
                                },
                                label = { Text("Açıklama ${index + 1}") },
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            descriptions.removeAt(descriptions.lastIndex)
                            openDialog.value = false
                            viewModel.viewModelScope.launch {
                                val surveyItem = SurveyItem(
                                    type = "_",
                                    questions = listOf(
                                        Question.SurveyTitle(
                                            title = title,
                                            description = descriptions
                                        )
                                    )
                                )
                                surveyItemList.add(surveyItem)

                                val uuid = UUID.randomUUID().toString()
                                val path = JsonHelper().saveJsonToFile(
                                    context = context,
                                    fileName = uuid,
                                    jsonData = Json.encodeToString(surveyItemList.toList())
                                )
                                val id = viewModel.saveJsonFileToDB(
                                    fileName = uuid,
                                    filePath = path,
                                    title = title
                                )
                                navController.navigate(CreateSurvey(title = title, id = id.toInt()))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(48.dp)
                        .width(120.dp)
                ) {
                    Text("Oluştur", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        title = ""
                        descriptions.clear()
                        descriptions.add("")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(48.dp)
                        .width(120.dp)
                ) {
                    Text("İptal", fontWeight = FontWeight.Medium)
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
} 