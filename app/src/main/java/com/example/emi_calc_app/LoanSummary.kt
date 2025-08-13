package com.example.emi_calc_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emi_calc_app.view_model.InputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSummary(
    viewModel: InputViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val emi = viewModel.calcEmi()
    val interest = viewModel.calcTotalInterest(emi)
    val total = viewModel.calcTotalAmountPayable(emi)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Summary") },
                navigationIcon = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Monthly EMI:")
                        Spacer(Modifier.height(2.dp))
                        Text("₹ %.2f".format(emi))
                    }
                }






                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Total Interest:")
                        Spacer(Modifier.height(2.dp))
                        Text("₹ %.2f".format(interest))
                    }
                }

                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Total Amount\n(Principal + Interest):")
                        Spacer(Modifier.height(2.dp))
                        Text("₹ %.2f".format(total))
                    }
                }


                Button(
                    onClick = {
                        navController.navigate("table")
                    }
                ) {
                    Text("Show Amortization Table")
                }
            }

        }
    }
}
