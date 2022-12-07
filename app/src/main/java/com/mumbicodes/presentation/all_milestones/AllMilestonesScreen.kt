package com.mumbicodes.presentation.all_milestones

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mumbicodes.R
import com.mumbicodes.presentation.theme.*

@Composable
fun AllMilestonesScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(Space24dp))
        val illustration = R.drawable.under_construction_illustration

        Image(
            modifier = Modifier.height(200.dp),
            painter = painterResource(id = illustration),
            contentDescription = "Empty state illustration"
        )

        Spacer(modifier = Modifier.height(Space36dp))

        Text(
            text = stringResource(id = R.string.allMilestones),
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        )
        Spacer(modifier = Modifier.height(Space16dp))

        val emptyText: String = stringResource(id = R.string.coming_soon)

        Text(
            modifier = Modifier.padding(start = Space32dp, end = Space32dp),
            text = emptyText,
            style = MaterialTheme.typography.bodyMedium.copy(GreyNormal),
            textAlign = TextAlign.Center
        )
    }
}
