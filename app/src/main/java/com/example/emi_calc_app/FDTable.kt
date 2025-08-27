package com.example.emi_calc_app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emi_calc_app.data.FdBreakdown

@Composable
fun FdTableView(
    fdTable: List<FdBreakdown>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableHeader("Month")
            TableHeader("Opening")
            TableHeader("Interest")
            TableHeader("Closing")
        }

        LazyColumn {
            items(fdTable) { row ->
                FdTableRow(row)
                Divider()
            }
        }
    }
}

@Composable
private fun TableHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun FdTableRow(breakdown: FdBreakdown) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(breakdown.month)
        TableCell("₹ %.2f".format(breakdown.principalComponent))
        TableCell("₹ %.2f".format(breakdown.interestComponent))
        TableCell("₹ %.2f".format(breakdown.totalAmount))
        TableCell("₹ %.2f".format(breakdown.growthPercent))
    }
}

@Composable
private fun TableCell(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}
