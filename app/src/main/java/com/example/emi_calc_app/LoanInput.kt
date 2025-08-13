package com.example.emi_calc_app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emi_calc_app.data.TenureUnit
import com.example.emi_calc_app.view_model.InputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanInput(
    viewModel: InputViewModel = viewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val input = viewModel.inputState
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI CALCULATOR") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = input.principal,
                onValueChange = {
                    if (it.isDigitsOnly() && it.length <= 10) {
                        viewModel.setPrincipalAmount(it)
                    }
                },
                label = { Text("Principal") },
                placeholder = { Text("Enter loan amount") },
                trailingIcon = {
                    Icon(Icons.Default.CurrencyRupee, contentDescription = "Rupee")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = input.interest,
                onValueChange = {
                    val num = it.toDoubleOrNull() ?: 0.0
                    if (num >= 0 && num <= 100) {
                        if (it.matches(Regex("(^\\d+)?(\\.\\d*)?$"))) {
                            viewModel.setInterestRate(it)
                        }
                    }
                },
                label = { Text("Interest rate") },
                placeholder = { Text("Enter interest rate") },
                trailingIcon = {
                    Icon(Icons.Default.Percent, contentDescription = "Percent")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = input.tenure,
                onValueChange = {
                    if (it.isDigitsOnly() && it.length <= 4) {
                        viewModel.setTenure(it)
                    }
                },
                label = { Text("Tenure") },
                placeholder = { Text("Enter loan tenure") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Select Tenure Period:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = input.tenureUnit == TenureUnit.MONTHS,
                    onClick = { viewModel.setTenureUnit(TenureUnit.MONTHS) }
                )
                Text("Months", modifier = Modifier.padding(end = 16.dp))

                RadioButton(
                    selected = input.tenureUnit == TenureUnit.YEARS,
                    onClick = { viewModel.setTenureUnit(TenureUnit.YEARS) }
                )
                Text("Years")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.navigate("table")
                    },
                    enabled = input.principal.isNotEmpty() && input.interest.isNotEmpty() && input.tenure.isNotEmpty()
                ) {
                    Text("View")
                }
                Button(onClick = {
                    viewModel.setInterestRate("")
                    viewModel.setPrincipalAmount("")
                    viewModel.setTenure("")
                }) {
                    Text("Clear")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(top = 12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Loan Summary")
                    if (expanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LoanSummary(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LoanSummary(
    viewModel: InputViewModel,
    modifier: Modifier = Modifier
) {
    val emi = viewModel.calcEmi()
    val interest = viewModel.calcTotalInterest(emi)
    val total = viewModel.calcTotalAmountPayable(emi)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Monthly EMI:")
                Spacer(Modifier.height(4.dp))
                Text("₹ %.2f".format(emi))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Interest:")
                Spacer(Modifier.height(4.dp))
                Text("₹ %.2f".format(interest))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Amount\n(Principal + Interest):")
                Spacer(Modifier.height(4.dp))
                Text("₹ %.2f".format(total))
            }
        }
    }
}
