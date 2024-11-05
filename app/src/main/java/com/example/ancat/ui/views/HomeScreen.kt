import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun DynamicTextFieldExample() {

    val textFields = remember { mutableStateListOf("") }

    Column {

        textFields.forEachIndexed { index, text ->
            TextField(
                value = text,
                onValueChange = { newText ->
                    textFields[index] = newText

                    if (newText.isNotEmpty() && index == textFields.size - 1) {
                        textFields.add("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
