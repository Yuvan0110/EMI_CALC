//package com.example.emi_calc_app
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material3.DatePicker
//import androidx.compose.material3.DatePickerDialog
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.rememberDatePickerState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.emi_calc_app.viewModelRepository.ViewModelRepository
//import java.text.SimpleDateFormat
//import java.time.LocalDate
//import java.time.ZoneId
//import java.util.Date
//import java.util.Locale
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StartDateField(
//    viewModelRepository: ViewModelRepository,
//    modifier: Modifier
//) {
//    var showDatePicker by remember { mutableStateOf(false) }
//    val selectedDate = remember { mutableStateOf(viewModelRepository.startDateTenure) }
//    val datePickerState = rememberDatePickerState(localDateToMillis(selectedDate.value))
//    OutlinedTextField(
//        value = selectedDate.value,
//        onValueChange = {},
//        label = { Text("Start date") },
//        trailingIcon = {
//            IconButton(onClick = { showDatePicker = true }) {
//                Icon(Icons.Default.DateRange, contentDescription = "Start Date")
//            }
//        },
//        readOnly = true,
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//    )
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let {
//                            selectedDate.value = it
//                            viewModelRepository.setStartDate(it)
//                        }
//                        showDatePicker = false
//                    }
//                ) { Text("OK") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
//
//fun localDateToMillis(localDate: LocalDate?): Long {
//    return localDate
//        ?.atStartOfDay(ZoneId.systemDefault())
//        ?.toInstant()
//        ?.toEpochMilli()
//        ?: 0
//}
//fun convertMillisToDate(millis: Long): String {
//    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//    return formatter.format(Date(millis))
//}