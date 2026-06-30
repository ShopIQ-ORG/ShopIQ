package com.iti.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

private val ILLUSTRATION_SIZE = 260.dp
private val BUTTON_HEIGHT = 52.dp
private val SCREEN_PADDING = 24.dp
private val ELEMENT_SPACING_LARGE = 24.dp
private val ELEMENT_SPACING_MEDIUM = 16.dp
private val ELEMENT_SPACING_SMALL = 8.dp
private val CORNER_RADIUS = 12.dp

@Composable
fun NoResultsScreen(
    query: String,
    onTryAnotherSearch: () -> Unit,
    onBrowseCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtitleText = if (query.isNotBlank()) {
        stringResource(id = R.string.no_results_subtitle, query)
    } else {
        stringResource(id = R.string.no_results_subtitle_generic)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_match_data),
            contentDescription = stringResource(id = R.string.no_results_title),
            modifier = Modifier.size(ILLUSTRATION_SIZE),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_LARGE))

        Text(
            text = stringResource(id = R.string.no_results_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_SMALL))

        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = SCREEN_PADDING)
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_LARGE))

        Button(
            onClick = onTryAnotherSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT),
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ELEMENT_SPACING_SMALL))
            Text(
                text = stringResource(id = R.string.no_results_btn_search),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_MEDIUM))

        OutlinedButton(
            onClick = onBrowseCategories,
            modifier = Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT),
            shape = RoundedCornerShape(CORNER_RADIUS)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home_fill),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(ELEMENT_SPACING_SMALL))
            Text(
                text = stringResource(id = R.string.no_results_btn_browse),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
