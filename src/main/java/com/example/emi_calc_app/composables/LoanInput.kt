package com.example.emi_calc_app.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emi_calc_app.data.TenureUnit
import com.example.emi_calc_app.viewModelRepository.ViewModelRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanInput(
    modifier: Modifier,
    viewModelRepository: ViewModelRepository,
    navController: NavController,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val tenureUnits = listOf("Years", "Months")

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = viewModelRepository.inputState.principal,
            onValueChange = { viewModelRepository.onPrincipalChange(it) },
            label = { Text("Principal") },
            placeholder = { Text("Enter loan amount") },
            trailingIcon = {
                Icon(Icons.Default.CurrencyRupee, contentDescription = "Rupee")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModelRepository.inputState.interest,
            onValueChange = { viewModelRepository.onInterestChange(it) },
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
                value = viewModelRepository.inputState.tenure,
                onValueChange = { viewModelRepository.onTenureChange(it) },
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
                    value = when (viewModelRepository.inputState.tenureUnit) {
                        TenureUnit.YEARS -> "Years"
                        TenureUnit.MONTHS -> "Months"
                    },
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
                                viewModelRepository.setTenureUnit(
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
                enabled = viewModelRepository.isValid()
            ) {
                Text("Detailed statistics")
            }

            Button(
                onClick = { navController.navigate("graph") },
                enabled = viewModelRepository.isValid()
            ) {
                Text("Show Chart")
            }
        }
    }
}






