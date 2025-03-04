package edu.aibu.ancat.ui.views.create_screen.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.navigation.CreateSurvey
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.create_screen.CreateScreenViewModel
import kotlinx.coroutines.launch

/**
 * JSON dosyalarının listelendiği ekran
 * 
 * @param modifier Modifier
 * @param navController Navigasyon kontrolcüsü
 * @param jsonFilesList JSON dosyalarının listesi
 * @param context Context
 * @param viewModel ViewModel
 */
@Composable
fun JsonFileListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    jsonFilesList: MutableList<JsonFilesInfoEntity>,
    context: Context,
    viewModel: CreateScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mevcut Anketler",
            modifier = modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(jsonFilesList) { item ->
                SurveyCard(
                    title = item.title,
                    lastModified = viewModel.convertTime(item.lastModified),
                    onClick = {
                        navController.navigate(CreateSurvey(item.title, item.id))
                    },
                    onDelete = {
                        if (JsonHelper().removeJsonFile(
                                fileName = item.fileName,
                                context = context
                            )
                        ) {
                            jsonFilesList.remove(item)
                            viewModel.viewModelScope.launch {
                                viewModel.deleteJsonFile(item)
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Anket kartı
 * 
 * @param title Anket başlığı
 * @param lastModified Son düzenleme tarihi
 * @param onClick Tıklama olayı
 * @param onDelete Silme olayı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyCard(
    title: String,
    lastModified: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Son Düzenleme: $lastModified",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Sil",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onDelete() }
            )
        }
    }
} 