package edu.aibu.ancat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableFloatingActionButton(
    modifier: Modifier,
    showBottomSheet: MutableState<Boolean>,
    onSaveButtonClicked: () -> Unit,
    onCreateButtonClicked: () -> Unit

) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
    ) {

        // Ana FloatingActionButton
        FloatingActionButton(
            modifier = modifier
                .padding(8.dp)
                .width(48.dp)
                .height(48.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(3.dp),
            onClick = { expanded = !expanded }
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
                modifier = modifier.padding(end = 8.dp, bottom = 64.dp)
            ) {
                // Kaydet
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .width(120.dp)
                        .height(48.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(3.dp),
                    onClick = {
                        expanded = false
                        onSaveButtonClicked()
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save"
                        )
                    },
                    text = {
                        Text(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            text = "Kaydet"
                        )
                    }
                )

                // Soru Ekle
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .width(140.dp)
                        .height(48.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(3.dp),
                    onClick = {
                        expanded = false
                        showBottomSheet.value = true
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Question"
                        )
                    },
                    text = {
                        Text(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            text = "Soru Ekle"
                        )
                    }
                )

                // PDF Oluştur
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(3.dp),
                    onClick = {
                        expanded = false
                        onCreateButtonClicked()
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Build,
                            contentDescription = "Create"
                        )
                    },
                    text = {
                        Text(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            text = "Anket Oluştur"
                        )
                    }
                )
            }
        }
    }
}