package com.example.ancat.ui.views.create_survey_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ancat.ui.component.FloatingMenuScreen

@Composable
fun CreateSurveyScreen(modifier: Modifier = Modifier, title: String) {
    SurveyCreator(title)

}


@Composable
fun SurveyCreator(title: String) {
    var questions by remember { mutableStateOf(mutableListOf<String>()) }
    var newQuestion by remember { mutableStateOf("") }
    Scaffold(

        floatingActionButton = {
            FloatingMenuScreen(modifier = Modifier)
        }
    ) { innerPadding ->

            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title)
                questions.forEachIndexed { index, question ->
                    TextField(
                        value = question,
                        onValueChange = { questions[index] = it },
                        label = { Text("Soru ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TextField(
                    value = newQuestion,
                    onValueChange = { newQuestion = it },
                    label = { Text("Yeni Soru Ekle") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    if (newQuestion.isNotBlank()) {
                        questions.add(newQuestion)
                        newQuestion = ""
                    }
                }) {
                    Text("Soru Ekle")
                }
                Spacer(modifier = Modifier.height(16.dp))


            }

        }
    }





