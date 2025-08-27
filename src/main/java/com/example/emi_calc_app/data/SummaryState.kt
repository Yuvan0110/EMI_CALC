package com.example.emi_calc_app.data

data class Breakdown(
    val month: String,
    val principalComponent: Double,
    val interestComponent: Double,
    val totalAmount : Double,
    val balance: Double,
    val percent: Double
)

//data class FdBreakdown(
//    val month: String,
//    val principalComponent: Double,
//    val interestComponent: Double,
//    val totalAmount: Double,
//    val growthPercent: Double
//)
