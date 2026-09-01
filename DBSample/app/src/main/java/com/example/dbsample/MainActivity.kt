package com.example.dbsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dbsample.data.AppDatabase
import com.example.dbsample.data.CustomerRepository
import com.example.dbsample.ui.DBSampleApp
import com.example.dbsample.ui.theme.DBSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = CustomerRepository(database.customerDao())
        
        setContent {
            DBSampleTheme {
                DBSampleApp(repository)
            }
        }
    }
}
