package edu.aibu.ancat.ui.views.create_survey_screen.components.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.aibu.ancat.ui.views.create_survey_screen.CreateSurveyViewModel
import edu.aibu.ancat.ui.views.create_survey_screen.DialogType

/**
 * Özel modal bottom sheet
 *
 * @param show Gösterme durumu
 * @param viewModel ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModalBottomSheet(
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Soru Tipi Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                QuestionTypeButton(
                    title = "Açıklama Metni",
                    description = "Kullanıcılara bilgi vermek için metin ekleyin",
                    onClick = {
                        show.value = false
                        viewModel.showDialog(DialogType.DescriptionType)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuestionTypeButton(
                    title = "Çoktan Seçmeli",
                    description = "Kullanıcıların seçenekler arasından seçim yapmasını sağlayın",
                    onClick = {
                        show.value = false
                        viewModel.showDialog(DialogType.MultipleChoiceType)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuestionTypeButton(
                    title = "Derecelendirme Sorusu",
                    description = "Kullanıcıların bir ölçekte derecelendirme yapmasını sağlayın",
                    onClick = {
                        show.value = false
                        viewModel.showDialog(DialogType.RatingType)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Soru tipi butonu
 *
 * @param title Başlık
 * @param description Açıklama
 * @param onClick Tıklama olayı
 */
@Composable
fun QuestionTypeButton(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 