package edu.aibu.ancat.ui.views.test_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TestView() {
    val viewModel: TestViewModel = hiltViewModel()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { CoroutineScope(Dispatchers.Main).launch {
                viewModel.createDoc(context)
            } }
        ) {
            Text(text = "Doküman Oluştur")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTestView() {
    TestView()
}
