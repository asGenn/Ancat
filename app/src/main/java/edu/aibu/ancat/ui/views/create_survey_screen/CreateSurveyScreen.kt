package edu.aibu.ancat.ui.views.create_survey_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.components.CustomExtendedFloatingActionButton
import edu.aibu.ancat.ui.views.components.CustomFloatingActionButton
import edu.aibu.ancat.ui.views.create_survey_screen.components.bottomsheet.CustomModalBottomSheet
import edu.aibu.ancat.ui.views.create_survey_screen.components.dialog.DialogHandler
import edu.aibu.ancat.ui.views.create_survey_screen.components.common.EmptySurveyContent
import edu.aibu.ancat.ui.views.create_survey_screen.components.common.SurveyCreatorTopBar
import edu.aibu.ancat.ui.views.create_survey_screen.components.item.SurveyItemsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Anket oluşturma ekranı
 *
 * @param id Anket ID'si
 */
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

/**
 * Anket oluşturucu ana ekranı
 *
 * @param modifier Modifier
 * @param viewModel ViewModel
 * @param surveyItem Anket öğeleri listesi
 * @param jsonFilesInfoEntity Anket dosya bilgisi
 */
@Composable
fun SurveyCreator(
    modifier: Modifier = Modifier,
    viewModel: CreateSurveyViewModel,
    surveyItem: List<SurveyItem>,
    jsonFilesInfoEntity: JsonFilesInfoEntity
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showSaveConfirmation = remember { mutableStateOf(false) }

    Column {
        SurveyCreatorTopBar(
            title = jsonFilesInfoEntity.title,
            onSaveClick = {
                viewModel.saveJson(context, jsonFilesInfoEntity, surveyItem)
                showSaveConfirmation.value = true
            }
        )
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            if (surveyItem.isEmpty()) {
                EmptySurveyContent()
            } else {
                SurveyItemsList(
                    surveyItems = surveyItem,
                    modifier = modifier
                )
            }

            // Kaydetme onay mesajı
            this@Column.AnimatedVisibility(
                visible = showSaveConfirmation.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Anket başarıyla kaydedildi",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                LaunchedEffect(showSaveConfirmation.value) {
                    scope.launch {
                        delay(2000)
                        showSaveConfirmation.value = false
                    }
                }
            }

            // Soru ekleme butonu (FAB)
            CustomFloatingActionButton(
                onClick = { showBottomSheet.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = Icons.Rounded.Add,
                contentDescription = "Soru Ekle"
            )

            // Anket oluşturma butonu
            CustomExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveJson(context, jsonFilesInfoEntity, surveyItem)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.createSurvey(context, jsonFilesInfoEntity)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                icon = Icons.Default.Check,
                text = "Anketi Oluştur"
            )
        }

        CustomModalBottomSheet(
            show = showBottomSheet,
            viewModel = viewModel
        )

        DialogHandler(viewModel = viewModel)
    }
}