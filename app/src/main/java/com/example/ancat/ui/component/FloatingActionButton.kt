package com.example.ancat.ui.component

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ancat.core.helper.JsonHelper
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ExpandableFloatingActionButton(
    modifier: Modifier,
    context: Context,
    jsonFilesInfoEntity: JsonFilesInfoEntity,
    surveyItem: List<SurveyItem>,
    showBottomSheet: MutableState<Boolean>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {

        // Ana FloatingActionButton
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = modifier
                .padding(8.dp).width(48.dp).height(48.dp),
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Menu"
            )
        }

        // Genişleyen butonlar
        if (expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.padding(end = 16.dp, bottom = 64.dp)
            ) {
                // Kaydet
                ExtendedFloatingActionButton(
                    onClick = {
                        expanded = false
                        JsonHelper().openFileAndWriteNewContent(
                            fileName = jsonFilesInfoEntity.fileName,
                            newContent = Json.encodeToString(surveyItem),
                            context = context
                        )
                    },
                    icon = { Icon(Icons.Default.Done, contentDescription = "Save") },
                    text = { Text("Kaydet") }
                )

                // Soru Ekle
                ExtendedFloatingActionButton(
                    onClick = {
                        expanded = false
                        showBottomSheet.value = true
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Question") },
                    text = { Text("Soru Ekle") }
                )

                // PDF Oluştur
                ExtendedFloatingActionButton(
                    onClick = {
                        /* TODO: Burada pdf oluşturulacak */
                    },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Create") },
                    text = { Text("Pdf Oluştur") }
                )
            }
        }
    }
}