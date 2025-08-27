package com.example.emi_calc_app.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.emi_calc_app.data.Breakdown

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Table(
    modifier: Modifier = Modifier,
    table : List<Breakdown>,
    headers : List<String>,
    navController: NavController
) {
    Scaffold(
        topBar =  {
            TopAppBar(
                title = { Text("Fixed deposit table") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = null
                        )
                    }
                },
                colors =TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
        ) {
//                StartDateField(
//                    viewModelRepository = ViewModelRepository(),
//                    modifier = Modifier
//                )
                BoxWithConstraints {

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
                            Row{
                                TableVal(
                                    headers[0],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold,
                                )
                                TableVal(
                                    headers[1],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold
                                )
                                TableVal(
                                    headers[2],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold,
                                )
                                TableVal(
                                    headers[3],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold
                                )
                                TableVal(
                                    headers[4],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold
                                )
                                TableVal(
                                    headers[5],
                                    dynamicColWidth,
                                    60.dp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                items(table) { row ->
                                    Row {
                                        TableVal(row.month,
                                            dynamicColWidth
                                        )
                                        TableVal(
                                            "%.2f".format(row.balance),
                                            dynamicColWidth
                                        )
                                        TableVal(
                                            "%.2f".format(row.principalComponent),
                                            dynamicColWidth
                                        )
                                        TableVal(
                                            "%.2f".format(row.interestComponent),
                                            dynamicColWidth
                                        )
                                        TableVal(
                                            "%.2f".format(row.totalAmount),
                                            dynamicColWidth
                                        )
                                        TableVal(
                                            "%.2f".format(row.percent),
                                            dynamicColWidth
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

@Composable
fun TableVal(
    text: String,
    width: Dp,
    height : Dp = 40.dp,
    fontWeight: FontWeight = FontWeight.Normal
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
            textAlign = TextAlign.Center,
            fontWeight = fontWeight
        )
    }
}