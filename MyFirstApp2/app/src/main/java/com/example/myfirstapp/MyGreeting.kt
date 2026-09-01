package com.example.myfirstapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A composable function that displays a greeting message with a background image.
 *
 * @param name The name of the person being greeted.
 * @param from The name of the person sending the greeting.
 * @param modifier The [Modifier] to be applied to the layout.
 */
@Composable
fun MyGreeting(name: String, from: String, modifier: Modifier = Modifier) {

    val image = painterResource(R.drawable.androidparty)
    Box(modifier) {
        Image(
            painter = image,
            contentScale = ContentScale.Crop,
            alpha = 0.5F,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = name,
                transitionSpec = {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                },
                label = "TextAnimation"
            ) { targetName ->
                Text(
                    text = stringResource(R.string.greeting_text, targetName),
                    fontSize = 36.sp,
                    lineHeight = 86.sp,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = stringResource(R.string.signature_text,from),
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.End)

            )
        }
    }
}

