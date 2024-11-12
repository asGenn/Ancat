package com.example.ancat.ui.views.create_survey_screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ancat.core.helper.JsonHelper
import com.example.ancat.core.helper.survey.SurveyHelper
import com.example.ancat.data.model.Question
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import com.example.ancat.ui.component.ExpandableFloatingActionButton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun CreateSurveyScreen(modifier: Modifier = Modifier, title: String, id: Int?) {
    val viewModel: CreateSurveyViewModel = hiltViewModel()
    val context = LocalContext.current
    val surveyItem = remember { mutableStateListOf<SurveyItem>() }

    val jsonFilesInfoEntity = remember { mutableStateOf<JsonFilesInfoEntity?>(null) }

    LaunchedEffect(Unit) {
        if (id != null) {
            val jsonFilesInfo = viewModel.getJsonFilesInfoById(id)

            jsonFilesInfoEntity.value = jsonFilesInfo

            Json.decodeFromString<List<SurveyItem>>(
                JsonHelper().readJsonFile(
                    context = context,
                    fileName = jsonFilesInfo.fileName
                )
            ).forEach {
                surveyItem.add(it)
            }


        }
    }
    if (jsonFilesInfoEntity.value != null) {
        SurveyCreator(
            title = title,
            viewModel = viewModel,
            surveyItem,
            jsonFilesInfoEntity = jsonFilesInfoEntity.value!!
        )
    }
}

@Composable
fun SurveyCreator(
    title: String,
    viewModel: CreateSurveyViewModel,
    surveyItem: MutableList<SurveyItem>,
    jsonFilesInfoEntity: JsonFilesInfoEntity
) {
    val showBottomSheet = remember { mutableStateOf(false) }

    val selectedItem = remember { mutableStateOf<SurveyItem?>(null) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                ExpandableFloatingActionButton(
                    modifier = Modifier,
                    context,
                    jsonFilesInfoEntity,
                    surveyItem,
                    showBottomSheet
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            itemsIndexed(surveyItem) { index, item ->
                when (item.type) {
                    "_" -> {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    item.title,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(top = 8.dp, bottom = 3.dp)
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                                surveyItem.forEach { it ->
                                    it.questions.forEachIndexed { index, question ->
                                        if (question is Question.SurveyTitle) {
                                            question.description.forEach {
                                                Text(
                                                    it,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    modifier = Modifier.padding(
                                                        horizontal = 12.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                Divider(
                                    color = Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()  // Çizginin tüm genişliği kaplamasını sağlar
                                        .padding(start = 5.dp, top = 8.dp, end = 5.dp)
                                )
                            }


                        }

                    }

                    "0" -> {
                        DescriptionType(
                            item = item,
                            selectedItem = selectedItem,
                            show = showBottomSheet,
                            viewModel = viewModel
                        )
                    }

                    "1" -> {
                        // RatingType,
                        RatingType(surveyItem = surveyItem)
                    }

                    "2" -> {
                        // MultipleChoiceType,
                        MultipleChoiceQuestion(surveyItem = surveyItem)
                    }
                }
            }
        }

        CustomModalBottomSheet(show = showBottomSheet, viewModel = viewModel)
        DialogHandler(viewModel = viewModel, surveyItem = surveyItem)

        selectedItem.value?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem.value = null },
                title = { Text(text = "Seçilen Öğe") },
                text = { Text("Öğe Başlığı: ${item.title}") },
                confirmButton = {
                    Button(onClick = { selectedItem.value = null }) {
                        Text("Kapat")
                    }
                }
            )
        }
    }
}

@Composable
private fun RatingType(modifier: Modifier = Modifier, surveyItem: MutableList<SurveyItem>) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        surveyItem.forEach { it ->
            it.questions.forEachIndexed { index, question ->
                if (question is Question.RatingQuestion) {
                    // Soru Başlığı
                    Text(
                        text = question.question,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    // Derecelendirme Seçenekleri
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            RadioButton(
                                selected = false,
                                onClick = { /*TODO*/ },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Ayırıcı Çizgi
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
    }
}


@Composable
fun MultipleChoiceQuestion(modifier: Modifier = Modifier, surveyItem: MutableList<SurveyItem>) {
    Column(
        modifier = modifier
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 0.dp)
            .fillMaxWidth()
    ) {
        surveyItem.forEach { item ->
            item.questions.forEachIndexed { _, question ->
                if (question is Question.MultipleChoiceQuestion) {
                    Text(
                        question.question,
                        modifier = modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,

                        )
                    question.options.forEachIndexed { index, option ->
                        Text(
                            "${index + 1}. (    ) $option",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()  // Çizginin tüm genişliği kaplamasını sağlar
                .padding(start = 5.dp, top = 8.dp, end = 5.dp)
        )

    }
}

@Composable
private fun DescriptionType(
    item: SurveyItem,
    modifier: Modifier = Modifier,
    selectedItem: MutableState<SurveyItem?>,
    show: MutableState<Boolean>,
    viewModel: CreateSurveyViewModel
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .background(color = Color.White)
            .fillMaxWidth()
            .clickable(onClick = {
                show.value = true
                viewModel.showDialog(DialogType.DescriptionType)
            }) // Show dialog
    ) {

    }
    item.questions.forEachIndexed { _, question ->
        if (question is Question.SimpleQuestion) {
            Text(
                question.question,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    modifier: Modifier = Modifier,
    show: MutableState<Boolean>,
    viewModel: CreateSurveyViewModel
) {
    val sheetState = rememberModalBottomSheetState()

    if (show.value) {
        ModalBottomSheet(
            onDismissRequest = {
                show.value = false
            },
            sheetState = sheetState,
            modifier = modifier.fillMaxSize(),

            ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.DescriptionType)
                }
            ) {
                Text("Açıklama Metni")
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.MultipleChoiceType)
                }
            ) {
                Text("Çoktan Seçmeli")
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.RatingType)
                }
            ) {
                Text("derecelendirme sorusu")
            }
        }
    }

}

@Composable
fun SimpleQuestionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    surveyItem: MutableList<SurveyItem>
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

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val survey = SurveyItem(
                        type = "0",
                        title = "Başlık",
                        questions = listOf(
                            Question.SimpleQuestion(
                                question = text
                            )
                        ),

                        )
                    surveyItem.add(survey)
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
    surveyItem: MutableList<SurveyItem>
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

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val survey = SurveyItem(
                        type = "1",
                        title = "Başlık",
                        questions = listOf(
                            Question.RatingQuestion(
                                question = text
                            ),

                            )
                    )
                    surveyItem.add(survey)
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
    surveyItem: MutableList<SurveyItem>
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
                        title = "Başlık",
                        questions = listOf(
                            Question.MultipleChoiceQuestion(
                                question = questionText,
                                options = options
                            )
                        )
                    )
                    surveyItem.add(survey)
                    onDismissRequest()
                }) {
                    Text("Ekle")
                }
            }
        }
    }
}


@Composable
fun DialogHandler(
    modifier: Modifier = Modifier,
    viewModel: CreateSurveyViewModel,
    surveyItem: MutableList<SurveyItem>
) {
    val dialogType by viewModel.dialogType.collectAsState()
    dialogType?.let {
        when (it) {
            DialogType.DescriptionType -> {


            }

            is DialogType.SurveyTitle -> {
                SimpleQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem
                )

            }


            DialogType.MultipleChoiceType -> {
                MultipleChoiceQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem
                )
            }

            DialogType.RatingType -> {
                RatingQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem
                )


            }
        }
    }


}




