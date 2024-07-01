package com.flingoapp.flingo.ui.components.common.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.components.common.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoTheme

@Composable
fun CustomHomeScreenTopBar(
    modifier: Modifier = Modifier,
    userName: String,
    onUserClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAwardClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
//        Text(
//            text = userName,
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentWidth(Alignment.CenterHorizontally),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineLarge
//        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // back navigation
            CustomElevatedTextButton(
                fontSize = 32,
                text = userName,
                onClick = { onUserClick() }
            )

            Row {
                // award button
                CustomIconButton(
                    icon = Icons.Default.Star,
                    iconPainter = painterResource(id = R.drawable.kid_star),
                    iconContentDescription = "Awards",
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    onClick = { onAwardClick() }
                )

                Spacer(modifier = Modifier.padding(12.dp))

                // settings button
                CustomIconButton(
                    icon = Icons.Default.Settings,
                    iconContentDescription = "Settings",
                    backgroundColor = Color.LightGray,
                    onClick = { onSettingsClick() }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CustomHomeScreenTopBarPreview() {
    FlingoTheme {
        CustomHomeScreenTopBar(
            userName = "Test",
            onUserClick = {},
            onSettingsClick = {},
            onAwardClick = {}
        )
    }
}