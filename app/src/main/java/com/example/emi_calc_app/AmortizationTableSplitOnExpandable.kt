
// Split table.kt
package com.example.emi_calc_app

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.emi_calc_app.view_model.InputViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.get

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AmortizationExpandableTableSplit(
    viewModel: InputViewModel,
    navController: NavController,
    modifier: Modifier
) {
    val yearlyData = viewModel.loadYearlyTable()
    val monthlyDataGrouped = viewModel.monthlyBreakdownGroupedByYear()
    val expandedYears = remember { mutableStateMapOf<String, Boolean>() }



    Scaffold(
        topBar = {
            AppBar("Amortization Table", onBack = { navController.navigateUp() })
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                StartDateField(
                    viewModel,
                    modifier.fillMaxWidth()
                )

                Spacer(modifier.height(16.dp))

                BoxWithConstraints {
                    val minColWidth = 120.dp
                    val dynamicColWidth = if (maxWidth / 5 < minColWidth) minColWidth else maxWidth / 5

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column {
                            Row {
                                TableCell("Year", dynamicColWidth, 56.dp)
                                TableCell("Principal", dynamicColWidth, 56.dp)
                                TableCell("Interest", dynamicColWidth, 56.dp)
                                TableCell("Total Amount\n(Principal + Interest)", dynamicColWidth + 50.dp, 56.dp)
                                TableCell("Balance", dynamicColWidth, 56.dp)
                                TableCell("Loan percentage\n paid", dynamicColWidth, 56.dp)
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(yearlyData) { yearEntry ->
                                    val year = yearEntry.month
                                    val isExpanded = expandedYears[year] ?: false

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    expandedYears[year] = !isExpanded
                                                }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(dynamicColWidth)
                                                    .height(40.dp)
                                                    .border(1.dp, Color.Gray),
                                                contentAlignment = Alignment.Center
                                            ){
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ){
                                                    Icon(
                                                        imageVector = if(isExpanded)Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = "expand"
                                                    )
                                                    Text(text = yearEntry.month,
                                                        fontSize = 14.sp,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                            TableCell("₹ %.2f".format(yearEntry.principalComponent), dynamicColWidth)
                                            TableCell("₹ %.2f".format(yearEntry.interestComponent), dynamicColWidth)
                                            TableCell("₹ %.2f".format(yearEntry.totalAmount), dynamicColWidth + 50.dp)
                                            TableCell("₹ %.2f".format(yearEntry.balance), dynamicColWidth)
                                            TableCell("%.2f".format(yearEntry.loanPercentPaid) + " %", dynamicColWidth)
                                        }
                                        if (isExpanded) {
                                            monthlyDataGrouped[year]?.forEach { monthEntry ->
                                                Row(modifier = Modifier.fillMaxWidth()) {
                                                    TableCell(monthEntry.month, dynamicColWidth)
                                                    TableCell("₹ %.2f".format(monthEntry.principalComponent), dynamicColWidth)
                                                    TableCell("₹ %.2f".format(monthEntry.interestComponent), dynamicColWidth)
                                                    TableCell("₹ %.2f".format(monthEntry.totalAmount), dynamicColWidth + 50.dp)
                                                    TableCell("₹ %.2f".format(monthEntry.balance), dynamicColWidth)
                                                    TableCell("%.2f".format(monthEntry.loanPercentPaid) + " %", dynamicColWidth)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable fun TableCell(
    text: String,
    width: Dp,
    height : Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.Center )
    {
        Text(
            text = text,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateField(
    viewModel: InputViewModel,
    modifier: Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(viewModel.startDateTenure) }
    val datePickerState = rememberDatePickerState(selectedDate.value)
    OutlinedTextField(
        value = convertMillisToDate(selectedDate.value),
        onValueChange = {},
        label = { Text("Start date") },
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker = true }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Start Date"
                )
            }
        },
        readOnly = true,
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    )
    if(showDatePicker){
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let{
                            selectedDate.value = it
                            viewModel.setStartDate(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ){
            DatePicker(state = datePickerState)
        }
    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

