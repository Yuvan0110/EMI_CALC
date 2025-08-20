package com.example.emi_calc_app

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emi_calc_app.view_model.InputViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter


@Composable
fun PieChartSummary(viewModel: InputViewModel = viewModel()) {
    val emi = viewModel.calcEmi()
    val interest = viewModel.calcTotalInterest(emi)
    val principal = viewModel.inputState.principal.toDoubleOrNull() ?: 0.0


    val entries = listOf(
        PieEntry(interest.toFloat(), "Interest"),
        PieEntry(principal.toFloat(), "Principal")
    )

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context)
        },
        update = { chart ->
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    Color.parseColor("#8BC34A"),
                    Color.parseColor("#36A2EB")
                )
                valueTextSize = 14f
                valueTextColor = Color.WHITE
                sliceSpace = 2f
            }

            val pieData = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(chart))
            }

            chart.data = pieData
            chart.setUsePercentValues(true)
            chart.description.isEnabled = false
            chart.isDrawHoleEnabled = true
            chart.setHoleColor(Color.TRANSPARENT)
            chart.setEntryLabelColor(Color.BLACK)
            chart.setEntryLabelTextSize(12f)
            chart.centerText = "Loan Breakdown"
            chart.setCenterTextSize(16f)

            chart.animateXY(1000, 1000)
            chart.invalidate()

            chart.legend.apply {
                isEnabled = true
                textSize = 15f
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }

        }
    )
}