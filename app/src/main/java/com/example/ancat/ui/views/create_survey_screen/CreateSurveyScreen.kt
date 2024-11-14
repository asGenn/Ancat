package com.example.ancat.ui.views.create_survey_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import com.example.ancat.ui.component.ExpandableFloatingActionButton
import com.example.ancat.ui.component.survey_item.DescriptionType
import com.example.ancat.ui.component.survey_item.MultipleChoiceQuestionDialog
import com.example.ancat.ui.component.survey_item.MultipleChoiceType
import com.example.ancat.ui.component.survey_item.RatingQuestionDialog
import com.example.ancat.ui.component.survey_item.RatingType
import com.example.ancat.ui.component.survey_item.SimpleQuestionDialog
import com.example.ancat.ui.component.survey_item.SurveyTitleType

@Composable
fun CreateSurveyScreen(id: Int?) {
    val viewModel: CreateSurveyViewModel = hiltViewModel()
    val context = LocalContext.current
    val jsonFilesInfoEntity = remember { mutableStateOf<JsonFilesInfoEntity?>(null) }
    val surveyItems by viewModel.surveyItems.collectAsState()

    LaunchedEffect(Unit) {
        id?.let {
            val jsonFilesInfo = viewModel.getJsonFilesInfoById(id)
            jsonFilesInfoEntity.value = jsonFilesInfo
            viewModel.loadSurveyItems(context, jsonFilesInfo)
        }
    }

    jsonFilesInfoEntity.value?.let {
        SurveyCreator(
            viewModel = viewModel,
            surveyItem = surveyItems,
            jsonFilesInfoEntity = jsonFilesInfoEntity.value!!
        )
    }
}


@Composable
fun SurveyCreator(
    modifier: Modifier = Modifier,
    viewModel: CreateSurveyViewModel,
    surveyItem: List<SurveyItem>,
    jsonFilesInfoEntity: JsonFilesInfoEntity
) {
    val showBottomSheet = remember { mutableStateOf(false) }

    val selectedItem = remember { mutableStateOf<SurveyItem?>(null) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                ExpandableFloatingActionButton(
                    modifier = modifier,
                    context = context,
                    jsonFilesInfoEntity = jsonFilesInfoEntity,
                    surveyItem = surveyItem,
                    showBottomSheet = showBottomSheet,
                    viewModel = viewModel
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            itemsIndexed(surveyItem) { _, item ->
                when (item.type) {
                    "_" -> SurveyTitleType(item = item)
                    "0" -> DescriptionType(item = item)
                    "1" -> RatingType(item = item)
                    "2" -> MultipleChoiceType(item = item)
                }
            }
        }

        CustomModalBottomSheet(modifier = modifier, show = showBottomSheet, viewModel = viewModel)
        DialogHandler(modifier = modifier, viewModel = viewModel, surveyItem = surveyItem)

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
fun DialogHandler(
    modifier: Modifier,
    viewModel: CreateSurveyViewModel,
    surveyItem: List<SurveyItem>
) {
    val dialogType by viewModel.dialogType.collectAsState()

    dialogType?.let { type ->
        when (type) {
            DialogType.DescriptionType -> {
                SimpleQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem,
                    viewModel = viewModel
                )

            }

            DialogType.MultipleChoiceType -> {
                MultipleChoiceQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem,
                    viewModel = viewModel
                )
            }

            DialogType.RatingType -> {
                RatingQuestionDialog(
                    modifier = modifier,
                    onDismissRequest = { viewModel.hideDialog() },
                    surveyItem = surveyItem,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
    modifier: Modifier,
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
                modifier = modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.DescriptionType)
                }
            ) {
                Text("Açıklama Metni")
            }
            TextButton(
                modifier = modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.MultipleChoiceType)
                }
            ) {
                Text("Çoktan Seçmeli")
            }
            TextButton(
                modifier = modifier.fillMaxWidth(),
                onClick = {
                    show.value = false
                    viewModel.showDialog(DialogType.RatingType)
                }
            ) {
                Text("Derecelendirme Sorusu")
            }
        }
    }

}