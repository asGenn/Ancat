package com.example.ancat.ui.views.create_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ancat.R
import com.example.ancat.core.navigation.CreateSurvey

@Composable
fun CreateScreen(navController: NavController) {

    EmptyScreen(navController =  navController)


}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier,navController: NavController) {

    val openDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty),
            contentDescription = "Empty",
            modifier = Modifier.height(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Her hangi bir anket oluşturmadınız.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Anket oluşturmak için aşağıdaki butona tıklayın.",
            fontSize = 14.sp
        )
        Button(onClick = {
//            navController.navigate(CreateSurvey)
            openDialog.value = true


        }
        ) {
            Text(
                text = "Anket Oluştur",
                fontSize = 16.sp,
                )
        }
    }
    SurveyTitleDialog(openDialog = openDialog, navController =  navController)

}

@Composable
fun SurveyTitleDialog(openDialog: MutableState<Boolean>, navController: NavController) {
    var title by remember { mutableStateOf("") }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Anket Başlığı") },
            text = {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık Girin") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {

                        openDialog.value = false
                        navController.navigate(CreateSurvey(title = title))

                    }
                ) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog.value = false }) {
                    Text("İptal")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}