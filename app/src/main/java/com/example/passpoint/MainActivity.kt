package com.example.passpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passpoint.ui.theme.PassPointTheme
import com.behametrics.logger.Logger


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.start(this)
        setContent {
            PassPointTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatternInput()
                }
            }
        }
    }
}

@Composable
fun PatternInput(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(3) { columnIndex ->
                        CircularButton(index = rowIndex * 3 + columnIndex + 1)
                    }
                }
            }
        }
    }
}

@Composable
fun CircularButton(index: Int) {
    Button(
        onClick = { /* Handle button click */ },
        shape = CircleShape,
        modifier = Modifier
            .size(64.dp) // Set button size
            .background(Color.LightGray, shape = CircleShape)
    ) {
        Text(text = index.toString(), color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun PatternInputPreview() {
    PassPointTheme {
        PatternInput()
    }
}
