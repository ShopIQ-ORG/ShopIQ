package com.iti.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R

private val ILLUSTRATION_SIZE = 200.dp
private val CONTENT_PADDING = 16.dp
private val SPACING_LARGE = 20.dp
private val SPACING_SMALL = 8.dp

@Composable
fun NoResultsFeedback(
    query: String,
    modifier: Modifier = Modifier
) {
    val subtitleText = if (query.isNotBlank()) {
        stringResource(id = R.string.no_results_subtitle, query)
    } else {
        stringResource(id = R.string.no_results_subtitle_generic)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(CONTENT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_match_data),
            contentDescription = stringResource(id = R.string.no_results_title),
            modifier = Modifier.size(ILLUSTRATION_SIZE),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(SPACING_LARGE))

        Text(
            text = stringResource(id = R.string.no_results_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(SPACING_SMALL))

        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = CONTENT_PADDING)
        )
    }
}
