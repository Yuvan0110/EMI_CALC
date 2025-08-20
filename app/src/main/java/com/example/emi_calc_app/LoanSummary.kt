package com.example.emi_calc_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emi_calc_app.view_model.InputViewModel


@Composable
fun LoanSummary(
    viewModel: InputViewModel,
    modifier: Modifier = Modifier
) {
    val emi = viewModel.calcEmi()
    val interest = viewModel.calcTotalInterest(emi)
    val total = viewModel.calcTotalAmountPayable(emi)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Monthly EMI:",
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(emi))
        }



        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Interest:",
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(interest))
        }



        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Amount\n(Principal + Interest):",
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(total))
        }

    }
}
