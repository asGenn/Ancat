package edu.aibu.ancat.ui.views.create_survey_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.create_survey_screen.components.CustomModalBottomSheet
import edu.aibu.ancat.ui.views.create_survey_screen.components.DialogHandler
import edu.aibu.ancat.ui.views.create_survey_screen.components.EmptySurveyContent
import edu.aibu.ancat.ui.views.create_survey_screen.components.SurveyCreatorTopBar
import edu.aibu.ancat.ui.views.create_survey_screen.components.SurveyItemsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    
    // Ekran boyutlarını al
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    
    // Responsive padding değerleri
    val horizontalPadding = screenWidth * 0.04f // Ekran genişliğinin %4'ü
    val bottomPadding = screenHeight * 0.02f // Ekran yüksekliğinin %2'si
    
    // Navigation bar yüksekliği için ek padding (yaklaşık 56dp standart yükseklik + güvenlik payı)
    val navBarHeight = 72.dp
    
    // Butonlar arası mesafe
    val buttonSpacing = screenHeight * 0.08f // Butonlar arası mesafe

    Scaffold(
        topBar = {
            SurveyCreatorTopBar(
                title = jsonFilesInfoEntity.title,
                onSaveClick = {
                    viewModel.saveJson(context, jsonFilesInfoEntity, surveyItem)
                    showSaveConfirmation.value = true
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            AnimatedVisibility(
                visible = showSaveConfirmation.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontalPadding)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
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
                        kotlinx.coroutines.delay(2000)
                        showSaveConfirmation.value = false
                    }
                }
            }

            // Soru ekleme butonu (FAB)
            FloatingActionButton(
                onClick = { showBottomSheet.value = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = horizontalPadding, bottom = bottomPadding + navBarHeight)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Soru Ekle",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Anket oluşturma butonu
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveJson(context, jsonFilesInfoEntity, surveyItem)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.createSurvey(context)
                    }
                },
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                },
                text = { Text("Anketi Oluştur") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                expanded = true,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = horizontalPadding, bottom = bottomPadding + navBarHeight)
            )
        }

        CustomModalBottomSheet(
            show = showBottomSheet,
            viewModel = viewModel
        )
        
        DialogHandler(viewModel = viewModel)
    }
}