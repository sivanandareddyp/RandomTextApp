package com.example.sivanandareddyapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sivanandareddyapplication.data.RandomTextRepository
import com.example.sivanandareddyapplication.data.local.AppDatabase
import com.example.sivanandareddyapplication.data.provider.IavProviderClient
import com.example.sivanandareddyapplication.ui.RandomTextViewModel
import com.example.sivanandareddyapplication.ui.screen.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.build(this)
        val dao = db.randomTextDao()
        val provider = IavProviderClient(contentResolver)
        val repo = RandomTextRepository(dao, provider)

        setContent {
            MaterialTheme {
                val vm: RandomTextViewModel = viewModel(factory = object: androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return RandomTextViewModel(repo) as T
                    }
                })
                HomeScreen(vm)
            }
        }
    }
}
