package com.mumbicodes.presentation.allProjects.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mumbicodes.R
import com.mumbicodes.presentation.theme.*

/**
 * TODO research on how to reduce the icon and text spacing and the whole margin
 * Todo - With the custom size < 56, the text is cut off - how to solve that
 */
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchParamType: String,
    searchedText: String = "",
    onSearchParamChanged: (String) -> Unit = {},
) {
    Surface(
        modifier = modifier
            // .height(Space48dp)
            .shadow(
                elevation = 60.dp,
                ambientColor = Color(0xFFCCCCCC).copy(alpha = 0.9f),
                spotColor = Color(0xFFCCCCCC).copy(alpha = 0.9f)
            ),
        shape = MaterialTheme.shapes.small,
        color = White,
    ) {
        TextField(
            value = searchedText,
            onValueChange = {
                onSearchParamChanged(it)
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier.alpha(0.5f),
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = GreySubtle
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = White,
                disabledTextColor = Color.Transparent,
                // Added below code to remove the underline
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f), // reduces the opacity
                    text = stringResource(id = R.string.search_placeHolder, searchParamType),
                    color = GreySubtle,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            textStyle = MaterialTheme.typography.bodySmall.copy(color = GreyNormal),

            singleLine = true,

            modifier = modifier
                .padding(0.dp)
                .heightIn(min = Space48dp)

        )
    }
}

@Preview
@Composable
fun SearchItemPreview() {
    ProjectTrackingTheme {
        SearchBar(searchParamType = "projects")
    }
}
