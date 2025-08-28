

// EmiApp.kt
package com.example.emi_calc_app


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emi_calc_app.composables.LoanInput
import com.example.emi_calc_app.composables.PieChartSummary
import com.example.emi_calc_app.composables.Summary
import com.example.emi_calc_app.composables.Table
import com.example.emi_calc_app.view_model.FdViewModel
import com.example.emi_calc_app.view_model.InputViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiApp() {
    val navController = rememberNavController()
    val inputViewModel: InputViewModel = viewModel()
    val fdViewModel : FdViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "input"
    ) {
        composable("input") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Finance Calculator") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    LoanInput(
                        modifier = Modifier.padding(paddingValues),
                        viewModelRepository = fdViewModel.viewModelRepository,
                        navController = navController
                    )
                    Summary(
                        fdViewModel.viewModelRepository.getPrincipal(),
                        fdViewModel.calcTotalInterest(),
                        fdViewModel.calcMaturityAmount(),
                        fdViewModel.stringVals
                    )
                    PieChartSummary(
                        fdViewModel.calcTotalInterest(),
                        fdViewModel.viewModelRepository.getPrincipal()
                    )
                }
            }
        }
        composable("table") {
            Table(
                modifier = Modifier,
                fdViewModel.loadFdTable(),
                fdViewModel.headers,
                navController = navController,
            )
        }
        composable("graph") {
            LineChartCompose(
                modifier = Modifier,
                fdViewModel.loadFdTable(),
                navController = navController
            )
        }
    }
}