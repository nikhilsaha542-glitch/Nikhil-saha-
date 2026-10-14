package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.local.AppDatabase
import com.example.data.repository.HistoryRepository
import com.example.ui.screens.CalculatorScreen
import com.example.ui.theme.NovaCalculatorTheme
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.CalculatorViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HistoryRepository(database.historyDao())
        CalculatorViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            NovaCalculatorTheme(isDarkTheme = uiState.isDarkTheme) {
                CalculatorScreen(viewModel = viewModel)
            }
        }
    }
}
