package com.example.ancat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ancat.R

@Composable
fun FloatingMenuScreen() {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
        // Sağ alta yerleştiriyoruz
    ) {
        // Menü butonu
        Button(
            onClick = { isMenuExpanded = !isMenuExpanded },
            modifier = Modifier
                .padding(16.dp)
                .width(64.dp)
                .height(64.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_add_24),
                contentDescription = "Add",
            )
        }

        // Menü elemanları
        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = expandVertically(),  // Yukarı doğru genişleyecek
            exit = shrinkVertically()    // Kapanırken aşağı doğru küçülecek
        ) {
            Column(
                modifier = Modifier.padding(top = 0.dp, end = 16.dp, bottom = 96.dp, start = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),  // Elemanlar arasındaki boşluk
                horizontalAlignment = Alignment.End  // Sağ tarafa hizalama
            ) {
                // Menü elemanlarını oluşturuyoruz
                Button(onClick = { /* Action for element 1 */ }) {
                    Text("Element 1")
                }
                Button(onClick = { /* Action for element 2 */ }) {
                    Text("Element 2")
                }
                Button(onClick = { /* Action for element 3 */ }) {
                    Text("Element 3")
                }
            }
        }
    }
}
