package edu.aibu.ancat.ui.views.analysis_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.aibu.ancat.core.navigation.AnalysisSurvey

/**
 * Analiz edilmiş dosyaların listelendiği ekran (sadece isim gösterir)
 *
 * @param data Dosya isim listesi (örnek: file1_result.json)
 */
@Composable
fun AnalyzedFileListScreen(
    navController: NavController,
    data: SnapshotStateList<Pair<String, String>>,
    callBack: (Pair<String, String>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Analiz Edilen Anketler",
            modifier = Modifier
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
            items(data) { item ->
                AnalyzedCard(
                    title = item.first,
                    onClick = {
                        navController.navigate(AnalysisSurvey(item.first, item.second))
                    },
                    onDelete = {
                        callBack(item)
                        // Buraya silme işlemi yapılabilir
                    }
                )
            }
        }
    }
}

/**
 * Analiz kartı bileşeni (yalnızca dosya ismi gösterir)
 *
 * @param title Dosya ismi
 * @param onClick Tıklama olayı
 * @param onDelete Silme olayı
 */
@Composable
fun AnalyzedCard(
    title: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

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
