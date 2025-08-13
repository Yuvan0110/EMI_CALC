package com.example.emi_calc_app

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emi_calc_app.view_model.InputViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationTable(
    modifier: Modifier = Modifier,
    viewModel: InputViewModel,
    navController: NavController
) {
    val table = viewModel.loadTable()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Amortization Table") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            StartDateField(viewModel)
            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints {
                val minColWidth = 120.dp
                val dynamicColWidth = if (maxWidth / 5 < minColWidth) minColWidth else maxWidth / 5

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                "Month",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(dynamicColWidth)
                            )
                            Text(
                                "Principal",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(dynamicColWidth)
                            )
                            Text(
                                "Interest",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(dynamicColWidth)
                            )
                            Text(
                                "Balance",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(dynamicColWidth)
                            )
                            Text(
                                "Loan %",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(dynamicColWidth)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            items(table) { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(row.month, modifier = Modifier.width(dynamicColWidth))
                                    Text(
                                        "%.2f".format(row.principalComponent),
                                        modifier = Modifier.width(dynamicColWidth)
                                    )
                                    Text(
                                        "%.2f".format(row.interestComponent),
                                        modifier = Modifier.width(dynamicColWidth)
                                    )
                                    Text(
                                        "%.2f".format(row.balance),
                                        modifier = Modifier.width(dynamicColWidth)
                                    )
                                    Text(
                                        "%.2f".format(row.loanPercentPaid),
                                        modifier = Modifier.width(dynamicColWidth)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateField(viewModel: InputViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(viewModel.startDateMillis) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.value
    )

    OutlinedTextField(
        value = convertMillisToDate(selectedDate.value),
        onValueChange = {},
        label = { Text("Start Date") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Select start date")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                IconButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) {
                        selectedDate.value = selected
                        viewModel.setStartDate(selected)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                IconButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}