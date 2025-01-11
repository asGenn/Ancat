package edu.aibu.ancat.ui.views.create_screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import edu.aibu.ancat.R
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.navigation.CreateSurvey
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.component.list.ListTile
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID


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

    Scaffold(
        floatingActionButton = {
            if (jsonFilesList.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        openDialog.value = true
                    },
                ) {
                    Text(
                        text = "Anket Oluştur",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

            }

        }


    ) { innerPadding ->
        if (jsonFilesList.isEmpty()) {
            EmptyScreen(
                openDialog = openDialog
            )


        } else {
            JsonFileListScreen(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                jsonFilesList = jsonFilesList,
                context = context,
                viewModel = viewModel
            )
        }


    }
    SurveyTitleDialog(
        openDialog = openDialog,
        navController = navController,
        context = context,
        viewModel
    )


}

@Composable
fun JsonFileListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    jsonFilesList: MutableList<JsonFilesInfoEntity>,
    context: Context,
    viewModel: CreateScreenViewModel
) {

    Column {
        Text(
            text = "Mevcut Anketler",
            modifier = modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )

        LazyColumn {
            items(jsonFilesList.size) { index ->
                val item = jsonFilesList[index]
                ListTile(
                    title = item.title,
                    subtitle = "Son Düzenleme:  " + viewModel.convertTime(item.lastModified),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                navController.navigate(CreateSurvey(item.title, item.id))
                            }
                        ),
                    trailingIcon = Icons.Default.Delete,
                    trailingIconButton = {

                        // delete file from storage
                        if (JsonHelper().removeJsonFile(
                                fileName = item.fileName,
                                context = context
                            )
                        ) {
                            // delete from list
                            jsonFilesList.remove(item)
                            // delete from database
                            viewModel.viewModelScope.launch {
                                viewModel.deleteJsonFile(item)
                            }


                        }


                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}


@Composable
fun EmptyScreen(
    openDialog: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty),
            contentDescription = "Empty",
            modifier = Modifier.height(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Her hangi bir anket oluşturmadınız.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Anket oluşturmak için aşağıdaki butona tıklayın.",
            fontSize = 14.sp
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Arka plan rengi
                contentColor = MaterialTheme.colorScheme.onPrimary // İçerik (metin) rengi
            ),
            onClick = {
                openDialog.value = true
            }
        ) {
            Text(
                text = "Anket Oluştur",
                fontSize = 16.sp,
            )
        }
    }


}

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
            modifier = Modifier.padding(16.dp).heightIn(min = 300.dp, max = 500.dp),
            onDismissRequest = {
                openDialog.value = false
                title = ""
                descriptions.clear()
                descriptions.add("")
            },
            title = { Text("Anket Başlığı", style = MaterialTheme.typography.headlineLarge) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Başlık Girin") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    LazyColumn {
                        itemsIndexed(descriptions) { index, desc ->
                            TextField(
                                value = desc,
                                onValueChange = {
                                    descriptions[index] = it
                                    if (it.isEmpty())
                                        descriptions.removeAt(descriptions.lastIndex)
                                    if (index == descriptions.lastIndex && it.isNotBlank())
                                        descriptions.add("")
                                },
                                label = { Text("Açıklama ${index + 1}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        descriptions.removeAt(descriptions.lastIndex)
                        openDialog.value = false
                        viewModel.viewModelScope.launch {
                            val surveyItem = SurveyItem(
                                type = "_",
                                questions = listOf(
                                    Question.SurveyTitle(title = title, description = descriptions)
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
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Onayla", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                Button(
                    onClick = { openDialog.value = false },
                ) {
                    Text("İptal", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}
