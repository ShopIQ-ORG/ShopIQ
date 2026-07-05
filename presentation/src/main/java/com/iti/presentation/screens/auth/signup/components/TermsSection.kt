package com.iti.presentation.screens.auth.signup.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.iti.presentation.R

@Composable
fun TermsSection(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    hasError: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = if (hasError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
            )
        )

        val annotatedText = buildAnnotatedString {
            append(stringResource(R.string.i_agree_to_the) + " ")

            withLink(LinkAnnotation.Clickable("terms") { onTermsClick() }) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.terms_of_service))
                }
            }

            append(" " + stringResource(R.string.and) + " ")

            withLink(LinkAnnotation.Clickable("privacy") { onPrivacyClick() }) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.privacy_policy))
                }
            }
        }

        Text(
            text = annotatedText,
            color = if (hasError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp
        )
    }
}
