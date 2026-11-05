package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MainMobiStreamApp
import com.example.ui.viewmodel.MobiStreamViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MobiStreamViewModel by viewModels {
        val app = application as MobiStreamApplication
        MobiStreamViewModel.provideFactory(app.container.channelRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainMobiStreamApp(viewModel = viewModel)
        }
    }
}
