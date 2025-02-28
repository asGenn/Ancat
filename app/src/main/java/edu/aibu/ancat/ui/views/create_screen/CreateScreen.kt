package edu.aibu.ancat.ui.views.create_screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import edu.aibu.ancat.ui.views.create_screen.components.EmptyScreen
import edu.aibu.ancat.ui.views.create_screen.components.JsonFileListScreen
import edu.aibu.ancat.ui.views.create_screen.components.SurveyTitleDialog

/**
 * Ana anket oluşturma ekranı
 * 
 * @param navController Navigasyon kontrolcüsü
 */
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
                FloatingActionButton(
                    onClick = { openDialog.value = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Anket Oluştur",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (jsonFilesList.isEmpty()) {
                EmptyScreen(
                    openDialog = openDialog
                )
            } else {
                JsonFileListScreen(
                    modifier = Modifier,
                    navController = navController,
                    jsonFilesList = jsonFilesList,
                    context = context,
                    viewModel = viewModel
                )
            }
        }
    }
    
    SurveyTitleDialog(
        openDialog = openDialog,
        navController = navController,
        context = context,
        viewModel = viewModel
    )
}

