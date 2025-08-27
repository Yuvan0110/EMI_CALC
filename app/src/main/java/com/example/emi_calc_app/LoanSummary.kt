package com.example.emi_calc_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emi_calc_app.view_model.InputViewModel


@Composable
fun Summary(
    fields : List<String>,
    viewModel: InputViewModel,
    modifier: Modifier = Modifier
) {
    val emi = viewModel.calcEmi()
    val interest = viewModel.calcTotalInterest(emi)
    val total = viewModel.calcTotalAmountPayable(emi)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Loan Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
//                    LoanSummary(viewModel)
            }
        }
        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fields[0],
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(emi))
        }



        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fields[1],
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(interest))
        }



        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fields[2],
                fontWeight = FontWeight.Bold
            )
            Text("₹ %.2f".format(total))
        }

    }
}
