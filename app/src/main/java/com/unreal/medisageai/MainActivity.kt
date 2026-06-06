package com.unreal.medisageai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unreal.medisageai.ui.chat.ChatScreen
import com.unreal.medisageai.ui.theme.MediSageAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediSageAITheme {
                ChatScreen()
            }
        }
    }
}
