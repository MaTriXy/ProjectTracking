package com.mumbicodes.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mumbicodes.presentation.theme.ProjectTrackingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: (String) -> Unit,
) {

    Surface(
        modifier = modifier,
        color = when {
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.primaryContainer
        },
        shape = MaterialTheme.shapes.small,
        onClick = { onClick(text) },
        contentColor = when {
            selected -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onBackground
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Preview
@Composable
fun ChipPreview() {
    ProjectTrackingTheme {
        FilterChip(
            text = "Test",
            selected = false,
            onClick = {}
        )
    }
}
