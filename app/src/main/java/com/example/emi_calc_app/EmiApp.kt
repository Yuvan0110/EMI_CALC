package com.example.emi_calc_app



import LoanInput
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emi_calc_app.view_model.InputViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmiApp() {
    val navController = rememberNavController()
    val viewModel: InputViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "input"
    ) {
        composable("input") {
            LoanInput(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable("table") {
            AmortizationTable(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
