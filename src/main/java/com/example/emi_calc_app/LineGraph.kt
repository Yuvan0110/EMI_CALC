package com.example.emi_calc_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.emi_calc_app.composables.AppBar
import com.example.emi_calc_app.data.Breakdown
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineChartCompose(
    modifier: Modifier = Modifier,
    yearlyData :List<Breakdown>,
    navController: NavController,
) {
//    val yearlyData = viewModel.loadYearlyTable()

    val year = yearlyData.map { it.month.takeLast(4).toFloat() }

    val balance = yearlyData.map { it.balance.toFloat() }
    val entriesYearBalance = year.zip(balance) { y, b -> Entry(y, b) }

    val principal = yearlyData.map { it.principalComponent.toFloat() }
    val entriesYearPrincipal = year.zip(principal) { y, p -> Entry(y, p) }

    val interest = yearlyData.map { it.interestComponent.toFloat() }
    val entriesYearInterest = year.zip(interest) { y, i -> Entry(y, i) }

    Scaffold(
        topBar = {
            AppBar("Chart Summary", onBack = { navController.navigateUp() })
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth(),
                factory = { context ->
                    val chart = LineChart(context)

                    // DataSets
                    val dataSetYearBalance = LineDataSet(entriesYearBalance, "Yearly Balance").apply {
                        color = Color.Cyan.toArgb()
                        setDrawValues(true)
                        lineWidth = 2f
                        setDrawCircles(true)
                    }

                    val dataSetYearPrincipal = LineDataSet(entriesYearPrincipal, "Yearly Principal").apply {
                        color = Color.Green.toArgb()
                        setDrawValues(true)
                        lineWidth = 2f
                        setDrawCircles(true)
                    }

                    val dataSetYearInterest = LineDataSet(entriesYearInterest, "Yearly Interest").apply {
                        color = Color.Yellow.toArgb()
                        setDrawValues(true)
                        lineWidth = 2f
                        setDrawCircles(true)
                    }

                    chart.data = LineData(dataSetYearBalance, dataSetYearPrincipal, dataSetYearInterest)

                    // Chart settings
                    chart.xAxis.apply {
                        granularity = 1f
                        position = XAxis.XAxisPosition.BOTTOM
                    }

                    chart.axisLeft.axisMinimum = 0f
                    chart.axisRight.isEnabled = false
                    chart.description.isEnabled = false
                    chart.legend.isEnabled = true

                    // Interaction
                    chart.setTouchEnabled(true)
                    chart.isDragEnabled = true
                    chart.setScaleEnabled(true)
                    chart.setPinchZoom(true)
                    chart.setVisibleXRangeMaximum(5f)

                    chart.animateXY(1000, 1000)
                    chart.invalidate()

                    chart
                }
            )
        }
    }
}







