package com.example.passpoint.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldWithLabel(label: String, textState: MutableState<String>, optional: Boolean) {
    Column {
        // Zobraziť label s "optional" ak je parameter optional true
        Text(
            text = buildString {
                append(label)
                if (optional) {
                    append(" (")
                    append("optional)")
                }
            },
            color = Color.Gray,
            fontWeight = FontWeight.Bold,  // Nastavenie textu na tučný
            modifier = Modifier.padding(start = 22.dp) // Padding na pravej strane
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Textové pole
        BasicTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)) // Zaoblené rohy
                .border(2.dp, Color.Gray, RoundedCornerShape(20.dp)) // Tmavo sivý border
                .padding(16.dp)
                .padding(start = 12.dp),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldWithLabelPreview() {
    // Sample state for preview
    val sampleTextState = remember { mutableStateOf("Sample text") }

    // Zobrazenie komponenty pre preview
    TextFieldWithLabel(label = "Username", textState = sampleTextState, optional = true)
}