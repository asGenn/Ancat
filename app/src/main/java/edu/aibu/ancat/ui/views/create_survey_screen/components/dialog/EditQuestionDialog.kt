package edu.aibu.ancat.ui.views.create_survey_screen.components.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun EditQuestionDialog(
    question: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedQuestion by remember { mutableStateOf(question) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Soruyu Düzenle") },
        text = {
            androidx.compose.material3.TextField(
                value = editedQuestion,
                onValueChange = { editedQuestion = it },
                label = { Text("Düzenlenecek Metin") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = { onConfirm(editedQuestion) }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            androidx.compose.material3.Button(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}