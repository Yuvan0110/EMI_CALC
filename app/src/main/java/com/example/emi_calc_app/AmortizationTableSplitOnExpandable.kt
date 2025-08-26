package com.example.emi_calc_app

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.emi_calc_app.data.EmiBreakdown
import com.example.emi_calc_app.view_model.InputViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AmortizationExpandableTableSplit(
    viewModel: InputViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val yearlyData = viewModel.loadYearlyTable()
    val monthlyDataGrouped = viewModel.monthlyBreakdownGroupedByYear()
    val expandedYears = remember { mutableStateMapOf<String, Boolean>() }
    val tableData = viewModel.loadTable() // For chart data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Amortization Table") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

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
                                                    .border(1.dp, ComposeColor.Gray),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = "expand"
                                                    )
                                                    Text(
                                                        text = yearEntry.month,
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

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { writeDataToPdf(viewModel) }
                    ) {
                        Text("Download as pdf")
                    }
                    Button(
                        onClick = { writeDataToExcel(viewModel) }
                    ) {
                        Text("Download as Excel")
                    }
                }
            }
        }
    }
}

//class CustomMarkerView(context: Context, private val tableData: List<EmiBreakdown>) : MarkerView(context, 0) {
//    private var tooltipTitle by mutableStateOf("Data Point")
//    private var tooltipValue by mutableStateOf("Value: N/A")
//
//    init {
//        val composeView = ComposeView(context).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                TooltipContent(title = tooltipTitle, value = tooltipValue)
//            }
//        }
//        addView(composeView)
//    }
//
//    @Composable
//    private fun TooltipContent(title: String, value: String) {
//        Column(
//            modifier = Modifier
//                .background(ComposeColor.White)
//                .padding(8.dp)
//                .border(1.dp, ComposeColor.Gray)
//        ) {
//            Text(
//                text = title,
//                fontWeight = FontWeight.Bold,
//                fontSize = 14.sp,
//                color = ComposeColor.Black
//            )
//            Text(
//                text = value,
//                fontSize = 12.sp,
//                color = ComposeColor.Black,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }
//
//    override fun refreshContent(e: Entry?, highlight: Highlight?) {
//        if (e == null) {
//            tooltipTitle = "No Data"
//            tooltipValue = ""
//        } else {
//            val index = e.x.toInt()
//            val entry = tableData.getOrNull(index)
//            if (entry != null) {
//                tooltipTitle = "Month: ${entry.month}"
//                tooltipValue = "Balance: ₹ ${String.format("%.2f", entry.balance)}\n" +
//                        "Principal: ₹ ${String.format("%.2f", entry.principalComponent)}\n" +
//                        "Interest: ₹ ${String.format("%.2f", entry.interestComponent)}"
//            } else {
//                tooltipTitle = "Month: ${index + 1}"
//                tooltipValue = "Balance: ₹ ${String.format("%.2f", e.y)}"
//            }
//        }
//        super.refreshContent(e, highlight)
//    }
//
//    override fun getOffset(): MPPointF {
//        return MPPointF(-(width / 2f), -height.toFloat())
//    }
//
//    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
//        val offset = getOffset()
//        if (posX + offset.x + width > chartView.width) {
//            offset.x = -width.toFloat()
//        }
//        if (posY + offset.y < 0) {
//            offset.y = 0f
//        }
//        return offset
//    }
//}



@Composable
fun TableCell(text: String, width: Dp, height: Dp = 40.dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .border(1.dp, ComposeColor.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDateField(viewModel: InputViewModel, modifier: Modifier) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(viewModel.startDateTenure) }
    val datePickerState = rememberDatePickerState(selectedDate.value)
    OutlinedTextField(
        value = convertMillisToDate(selectedDate.value),
        onValueChange = {},
        label = { Text("Start date") },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Start Date")
            }
        },
        readOnly = true,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate.value = it
                            viewModel.setStartDate(it)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
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

@SuppressLint("DefaultLocale")
fun writeDataToFile(viewModel: InputViewModel) {
    val dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val outputFile = File(dest, "summary_${System.currentTimeMillis()}.txt")
    val table = viewModel.loadTable()
    outputFile.bufferedWriter().use { writer ->
        writer.write("Month     Principal    Interest    Total Amount    Balance    Loan % Paid\n\n")
        table.forEach {
            writer.write("${it.month}     ${String.format("%.2f", it.principalComponent)}    ${String.format("%.2f", it.interestComponent)}    ${String.format("%.2f", it.totalAmount)}    ${String.format("%.2f", it.balance)}    ${String.format("%.2f", it.loanPercentPaid) + '%'} \n")
        }
    }
}

fun writeDataToExcel(viewModel: InputViewModel) {
    val workbook: Workbook = XSSFWorkbook()
    val sheet: Sheet = workbook.createSheet("SummaryData_${System.currentTimeMillis()}")
    val table = viewModel.loadTable()
    val row: Row = sheet.createRow(0)
    row.createCell(0).setCellValue("Month")
    row.createCell(1).setCellValue("Principal")
    row.createCell(2).setCellValue("Interest")
    row.createCell(3).setCellValue("Total Amount")
    row.createCell(4).setCellValue("Balance")
    row.createCell(5).setCellValue("Loan Loan Paid Percentage")
    for (i in 1..table.size) {
        val row: Row = sheet.createRow(i)
        row.createCell(0).setCellValue(table[i - 1].month)
        row.createCell(1).setCellValue(table[i - 1].principalComponent)
        row.createCell(2).setCellValue(table[i - 1].interestComponent)
        row.createCell(3).setCellValue(table[i - 1].totalAmount)
        row.createCell(4).setCellValue(table[i - 1].balance)
        row.createCell(5).setCellValue(table[i - 1].loanPercentPaid)
    }
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(folder, "SummaryData_${System.currentTimeMillis()}.xlsx")
    val fileOut = FileOutputStream(file)
    workbook.write(fileOut)
}

fun writeDataToPdf(viewModel: InputViewModel) {
    val tableData = viewModel.loadTable()
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(folder, "AmortizationTable_${System.currentTimeMillis()}.pdf")
    val document = Document()
    PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()
    val pdfTable = PdfPTable(6)
    pdfTable.widthPercentage = 100f
    val headers = listOf("Month", "Principal", "Interest", "Total Amount", "Balance", "Loan % Paid")
    headers.forEach {
        val cell = PdfPCell(Paragraph(it)).apply {
            horizontalAlignment = Element.ALIGN_CENTER
        }
        pdfTable.addCell(cell)
    }
    tableData.forEach {
        pdfTable.addCell(it.month)
        pdfTable.addCell("₹ %.2f".format(it.principalComponent))
        pdfTable.addCell("₹ %.2f".format(it.interestComponent))
        pdfTable.addCell("₹ %.2f".format(it.totalAmount))
        pdfTable.addCell("₹ %.2f".format(it.balance))
        pdfTable.addCell("%.2f %%".format(it.loanPercentPaid))
    }
    document.add(pdfTable)
    document.close()
}