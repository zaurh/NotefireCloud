package com.example.random.presentation

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.random.R
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun SplashScreen(
    navController: NavController
) {
    LaunchedEffect(key1 = Unit){
        delay(1000)
        navController.navigate("sign_in"){
            popUpTo(0)
        }
    }
    Box(modifier = Modifier.fillMaxHeight()) {
        Image(
            painter = painterResource(id = R.drawable.newlogo),
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.fillMaxHeight()
        )
    }
}