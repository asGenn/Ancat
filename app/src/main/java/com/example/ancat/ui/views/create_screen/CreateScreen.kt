package com.example.ancat.ui.views.create_screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.example.ancat.R
import com.example.ancat.core.helper.JsonHelper
import com.example.ancat.core.helper.TimeConverter
import com.example.ancat.core.navigation.CreateSurvey
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import com.example.ancat.ui.component.ListTile
import kotlinx.coroutines.launch
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
                    onClick = {
                        openDialog.value = true
                    },

                    ) {
                    Text(
                        text = "Anket Oluştur",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

            }

        }


    ) { innerPadding ->
        if (jsonFilesList.isEmpty()) {
            EmptyScreen(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                context = context,
                viewModel = viewModel,
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(jsonFilesList.size) { index ->
                val item = jsonFilesList[index]
                ListTile(
                    title = item.title,
                    subtitle = "Son Düzenleme: " + TimeConverter().convertTime(item.lastModified),
                    modifier = Modifier.padding(8.dp).clickable(
                        onClick = {
                            navController.navigate(CreateSurvey(item.title))
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
    modifier: Modifier = Modifier,
    navController: NavController,
    context: Context,
    viewModel: CreateScreenViewModel,
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
        Button(onClick = {
//            navController.navigate(CreateSurvey)
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


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Anket Başlığı") },
            text = {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık Girin") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {

                        openDialog.value = false
                        viewModel.viewModelScope.launch {

                            val uuid = UUID.randomUUID().toString()
                            val path = JsonHelper().saveJsonToFile(
                                context = context,
                                fileName = uuid,
                                jsonData = ""
                            )
                            viewModel.saveJsonFile(fileName = uuid, filePath = path, title = title)

                            viewModel.getJsonFiles().forEach(
                                {
                                    //epcoh time to date
                                    val date = TimeConverter().convertTime(it.lastModified)


                                    println(it.fileName + " " + date.toString())
                                    println(it.id)
                                    println(it.filePath)

                                }
                            )

                        }


                        navController.navigate(CreateSurvey(title = title))


                    }
                ) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog.value = false }) {
                    Text("İptal")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}

