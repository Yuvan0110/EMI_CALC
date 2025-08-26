package com.example.emi_calc_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emi_calc_app.data.TenureUnit
import com.example.emi_calc_app.view_model.InputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanInput(
    modifier: Modifier = Modifier,
    viewModel: InputViewModel = viewModel(),
    navController: NavController
) {
    val input = viewModel.inputState
    var isExpanded by remember { mutableStateOf(false) }
    val tenureUnits = listOf("Years", "Months")


    LaunchedEffect(Unit) {
        if (input.principal.isEmpty()) viewModel.setPrincipalAmount("1000000")
        if (input.interest.isEmpty()) viewModel.setInterestRate("7.5")
        if (input.tenure.isEmpty()) viewModel.setTenure("5")
    }

    val selectedUnit = when (input.tenureUnit) {
        TenureUnit.YEARS -> "Years"
        TenureUnit.MONTHS -> "Months"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI Calculator") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
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
                    if (num in 0.0..100.0) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input.tenure,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 4) {
                            viewModel.setTenure(it)
                        }
                    },
                    label = { Text("Tenure") },
                    placeholder = { Text("Enter loan tenure") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedUnit,
                        onValueChange = {},
                        label = { Text("Tenure Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )

                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        tenureUnits.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    isExpanded = false
                                    viewModel.setTenureUnit(
                                        if (selection == "Years") TenureUnit.YEARS else TenureUnit.MONTHS
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("table") },
                    enabled = input.principal.isNotEmpty() &&
                            input.interest.isNotEmpty() &&
                            input.tenure.isNotEmpty()
                ) {
                    Text("Detailed statistics")
                }

                Button(
                    onClick = { navController.navigate("graph") },
                    enabled = input.principal.isNotEmpty() &&
                            input.interest.isNotEmpty() &&
                            input.tenure.isNotEmpty()
                ) {
                    Text("Show Chart")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Loan Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LoanSummary(viewModel)
                }
            }

            PieChartSummary(viewModel)
        }
    }
}





