package com.innovature.castnow

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import com.innovature.castnow.api.ImageApi
import com.innovature.castnow.ui.screens.Slideshow
import com.innovature.castnow.ui.theme.CastNOWTheme
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageApi: ImageApi

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        setContent {
            CastNOWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    colors = SurfaceDefaults.colors(
                        containerColor = Color.Black
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .onPreviewKeyEvent { event ->

                                if (event.type == KeyEventType.KeyDown) {

                                    when (event.key) {

                                        Key.Zero -> {
                                            println("0 pressed")
                                            true
                                        }

                                        Key.One -> {
                                            println("1 pressed")
                                            true
                                        }

                                        Key.Two -> {
                                            println("2 pressed")
                                            true
                                        }

                                        Key.Three -> {
                                            println("3 pressed")
                                            true
                                        }

                                        Key.Four -> {
                                            println("4 pressed")
                                            true
                                        }

                                        Key.Five -> {
                                            println("5 pressed")
                                            true
                                        }

                                        Key.Six -> {
                                            println("6 pressed")
                                            true
                                        }

                                        Key.Seven -> {
                                            println("7 pressed")
                                            true
                                        }

                                        Key.Eight -> {
                                            println("8 pressed")
                                            true
                                        }

                                        Key.Nine -> {
                                            println("9 pressed")
                                            true
                                        }

                                        else -> false
                                    }

                                } else {
                                    false
                                }
                            }
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        Slideshow(imageApi)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}