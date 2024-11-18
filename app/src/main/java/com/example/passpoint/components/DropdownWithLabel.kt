package com.example.passpoint.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownWithLabel(
    items: List<String>,
    label: String = "Vyberte možnosť",
    textState: MutableState<String> // This state is passed from the parent
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Display the label (above the text field)
        Text(
            text = label,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 22.dp, top = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // TextField with dropdown icon
        TextField(
            value = textState.value, // The value of the text field is bound to textState
            onValueChange = {}, // No need to update this, as we're using textState
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            },
            readOnly = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent, // Set the background to transparent
                focusedIndicatorColor = Color.Gray, // Color for the indicator when the text field is focused
                unfocusedIndicatorColor = Color.Gray // Color for the indicator when the text field is not focused
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)) // Rounded corners
                .border(2.dp, Color.Gray, RoundedCornerShape(20.dp)) // Border around the text field
                .padding(start = 10.dp),
        )

        // Dropdown menu to show the items
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Make the menu take the full width
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        textState.value = item // Update textState when an item is selected
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownWithLabelPreview() {
    // Initialize selectedValue with the first item in the list
    val items = listOf("0-18", "18-25", "26-35", "36-45", "46-60", "60+")
    var selectedValue by remember { mutableStateOf(items.first()) } // Set the default to the first item

    MaterialTheme {
        DropdownWithLabel(
            items = items,
            label = "Vekový interval",
            textState = remember { mutableStateOf(selectedValue) } // Pass the state to the Dropdown
        )
    }
}