package com.example.emi_calc_app

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.emi_calc_app.ui.theme.EmicalcTheme
import com.example.emi_calc_app.view_model.InputViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmortizationTable(
    modifier: Modifier = Modifier,
    viewModel: InputViewModel,
    navController: NavController
) {

    var isSplit by remember { mutableStateOf(false) }
    val table = if(isSplit) viewModel.loadTable() else viewModel.loadYearlyTable()
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
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column{
                Box {
                    StartDateField(modifier, viewModel)
                }
                Row (
                    modifier.padding(end = 16.dp).align(Alignment.End)
                ){
                    AssistChip(
                        onClick = {
                            isSplit = !isSplit
                        },
                        label = {
                            if (isSplit) Text("Monthly Split") else Text("Yearly Split")
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Switch Split"
                            )
                        }
                    )
                }
                Spacer(modifier.height(12.dp))

                BoxWithConstraints(
                    modifier = modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                ) {

                    //            val columnWidths = listOf(80.dp, 120.dp, 120.dp, 120.dp, 120.dp)
                    val minColWidth = 120.dp

                    val dynamicColWidth =
                        if (maxWidth / 5 < minColWidth) minColWidth else maxWidth / 5
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    if(isSplit)"Month" else "Year",
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

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateField(
    modifier: Modifier = Modifier,
    viewModel: InputViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(viewModel.startDateTenure) }
    val datePickerState = rememberDatePickerState(selectedDate.value)
    OutlinedTextField(
        value = viewModel.convertMillisToDate(selectedDate.value),
        onValueChange = {},
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
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
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



@Composable
fun SplitChip(modifier: Modifier = Modifier) {

    var isSplit by remember { mutableStateOf(false) }

    AssistChip(
        onClick = {
            isSplit = !isSplit
        },
        label = {
            if(isSplit)Text("Yearly Split")else Text("Monthly Split")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TablePreview(modifier: Modifier = Modifier) {
    EmicalcTheme {
        SplitChip()
    }
}