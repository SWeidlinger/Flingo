package com.flingoapp.flingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.flingoapp.flingo.ui.FlingoApp
import com.flingoapp.flingo.viewmodels.ViewModelFactory
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainViewModel

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mainViewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]

        val userJson = this.assets.open("mock_user.json").bufferedReader().use { it.readText() }
        mainViewModel.onAction(MainIntent.OnMockFetchData(userJson))

        setContent {
            FlingoApp(mainViewModel = mainViewModel)
        }
    }
}