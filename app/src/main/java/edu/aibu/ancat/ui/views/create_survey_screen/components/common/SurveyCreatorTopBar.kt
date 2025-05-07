package edu.aibu.ancat.ui.views.create_survey_screen.components.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/**
 * Anket oluşturucu üst çubuğu
 * 
 * @param title Anket başlığı
 * @param onSaveClick Kaydetme butonu tıklama olayı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyCreatorTopBar(
    title: String,
    onSaveClick: () -> Unit
) {
    TopAppBar(
        {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        windowInsets = WindowInsets(8, 0, 0, 8),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Kaydet",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    )
} 